package net.coma112.ctoken.database;

import lombok.Getter;
import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.keys.ConfigKeys;
import net.coma112.ctoken.events.BalanceAddEvent;
import net.coma112.ctoken.events.BalanceResetEvent;
import net.coma112.ctoken.events.BalanceSetEvent;
import net.coma112.ctoken.events.BalanceTakeEvent;
import net.coma112.ctoken.manager.TokenTop;
import net.coma112.ctoken.utils.TokenLogger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
public class SQLite extends AbstractDatabase {
    private final Connection connection;

    public SQLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + new File(CToken.getInstance().getDataFolder(), "token.db"));
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                TokenLogger.error(exception.getMessage());
            }
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS token (PLAYER VARCHAR(255) NOT NULL, BALANCE INT, XP INT DEFAULT 0, PRIMARY KEY (PLAYER))";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }


    @Override
    public void createPlayer(@NotNull OfflinePlayer player) {
        String query = "INSERT INTO token (PLAYER, BALANCE, XP) VALUES (?, ?, ?)";

        try {
            if (!exists(player)) {
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                    preparedStatement.setString(1, player.getName());
                    preparedStatement.setInt(2, ConfigKeys.STARTING_BALANCE.getInt());
                    preparedStatement.setInt(3, calculateXPFromTokens(ConfigKeys.STARTING_BALANCE.getInt()));
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    @Override
    public boolean exists(@NotNull OfflinePlayer player) {
        String query = "SELECT * FROM token WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                return preparedStatement.executeQuery().next();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return false;
    }

    @Override
    public int getBalance(@NotNull OfflinePlayer player) {
        String query = "SELECT BALANCE FROM token WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("BALANCE");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
        return 0;
    }

    @Override
    public int getXP(@NotNull OfflinePlayer player) {
        String query = "SELECT XP FROM token WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("XP");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
        return 0;
    }

    @Override
    public List<TokenTop> getTop(int number) {
        List<TokenTop> topBalances = new ArrayList<>();
        String query = "SELECT PLAYER, BALANCE FROM token ORDER BY BALANCE DESC LIMIT ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    topBalances.add(new TokenTop(resultSet.getString("PLAYER"), resultSet.getInt("BALANCE")));
                }
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
        return topBalances;
    }

    @Override
    public String getTopPlayer(int top) {
        String playerName = null;
        String query = "SELECT PLAYER FROM token ORDER BY BALANCE DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) playerName = resultSet.getString("PLAYER");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return playerName;
    }

    @Override
    public int getTopBalance(int top) {
        String query = "SELECT BALANCE FROM token ORDER BY BALANCE DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("BALANCE");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public int getTopPlace(@NotNull OfflinePlayer player) {
        String query = "SELECT COUNT(*) FROM token WHERE BALANCE > (SELECT BALANCE FROM token WHERE PLAYER = ?)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getInt(1) + 1;
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return -1;
    }

    @Override
    public void setBalance(@NotNull OfflinePlayer player, int newBalance) {
        int oldBalance = getBalance(player);
        updateBalance(Objects.requireNonNull(player.getName()), newBalance);
        Bukkit.getServer().getPluginManager().callEvent(new BalanceSetEvent(player, oldBalance, newBalance));
    }

    @Override
    public void addToBalance(@NotNull OfflinePlayer player, int newBalance) {
        int oldBalance = getBalance(player);
        int updatedBalance = oldBalance + newBalance;
        updateBalance(Objects.requireNonNull(player.getName()), updatedBalance);
        Bukkit.getServer().getPluginManager().callEvent(new BalanceAddEvent(player, oldBalance, newBalance));
    }

    @Override
    public void addToEveryoneBalance(int newBalance) {
        String query = "SELECT PLAYER, BALANCE FROM token";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String playerName = resultSet.getString("PLAYER");
                    int currentBalance = resultSet.getInt("BALANCE");
                    int updatedBalance = currentBalance + newBalance;

                    updateBalance(playerName, updatedBalance);
                }
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    @Override
    public void resetBalance(@NotNull OfflinePlayer player) {
        int oldBalance = getBalance(player);
        updateBalance(Objects.requireNonNull(player.getName()), 0);
        Bukkit.getServer().getPluginManager().callEvent(new BalanceResetEvent(player, oldBalance));
    }


    @Override
    public void resetEveryone() {
        String query = "UPDATE token SET BALANCE = 0, XP = 0";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    @Override
    public void takeFromBalance(@NotNull OfflinePlayer player, int newBalance) {
        int oldBalance = getBalance(player);
        int updatedBalance = oldBalance - newBalance;
        updateBalance(Objects.requireNonNull(player.getName()), updatedBalance);
        Bukkit.getServer().getPluginManager().callEvent(new BalanceTakeEvent(player, oldBalance, newBalance));
    }

    @Override
    public int calculateXPFromTokens(int tokenBalance) {
        return (int) (tokenBalance * ConfigKeys.BADGES_MULTIPLIER.getDouble());
    }

    private void updateBalance(@NotNull String playerName, int newBalance) {
        String updateQuery = "UPDATE token SET BALANCE = ?, XP = ? WHERE PLAYER = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                updateStatement.setInt(1, newBalance);
                updateStatement.setInt(2, calculateXPFromTokens(newBalance));
                updateStatement.setString(3, playerName);
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }
}
