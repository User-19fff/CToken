package net.coma112.ctoken;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.coma112.ctoken.config.Config;
import net.coma112.ctoken.database.AbstractDatabase;
import net.coma112.ctoken.database.MySQL;
import net.coma112.ctoken.database.SQLite;
import net.coma112.ctoken.enums.DatabaseType;
import net.coma112.ctoken.enums.LanguageType;
import net.coma112.ctoken.enums.keys.ConfigKeys;
import net.coma112.ctoken.hooks.PlaceholderAPI;
import net.coma112.ctoken.language.Language;
import net.coma112.ctoken.utils.TokenLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

import static net.coma112.ctoken.utils.StartingUtils.*;

public final class CToken extends JavaPlugin {
    @Getter
    private static CToken instance;
    @Getter
    private static AbstractDatabase database;
    @Getter
    private Language language;
    @Getter
    private TaskScheduler scheduler;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();
        registerListenersAndCommands();
        initializeDatabaseManager();
        loadBasicFormatOverrides();

        new PlaceholderAPI().register();
    }

    @Override
    public void onDisable() {
        if (database != null) database.disconnect();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        config = new Config();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_es.yml");

        language = new Language("messages_" + LanguageType.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseType.valueOf(ConfigKeys.DATABASE.getString())) {
                case MYSQL, mysql -> {
                    database = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    MySQL mysql = (MySQL) database;
                    mysql.createTable();
                }

                case SQLITE, sqlite -> {
                    database = new SQLite();
                    SQLite sqlite = (SQLite) database;
                    sqlite.createTable();

                }
            }
        } catch (SQLException | ClassNotFoundException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }
}
