package net.coma112.ctoken.commands;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.MessageKeys;
import net.coma112.ctoken.manager.TokenTop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"ctoken", "token"})
@SuppressWarnings("deprecation")
public class CommandToken {
    @DefaultFor({"token", "ctoken"})
    public void defaultCommand(@NotNull CommandSender sender) {
        help(sender);
    }

    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        MessageKeys.HELP
                .getMessages()
                .forEach(sender::sendMessage);
    }

    @Subcommand("reload")
    @CommandPermission("ctoken.reload")
    public void reload(@NotNull CommandSender sender) {
        CToken.getInstance().getLanguage().reload();
        CToken.getInstance().getConfiguration().reload();
        sender.sendMessage("reload");
    }

    @Subcommand("add")
    @CommandPermission("ctoken.add")
    public void add(@NotNull CommandSender sender, @NotNull String input, int value) {
        if (value <= 0) {
            sender.sendMessage("Invalid value");
            return;
        }

        if (input.equals("*")) {
            CToken.getDatabase().addToEveryoneBalance(value);
            sender.sendMessage("Added " + value + " to everyone");
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage("nincsen");
            return;
        }

        CToken.getDatabase().addToBalance(target, value);
        sender.sendMessage("Added " + value + " to " + target.getName());
    }

    @Subcommand("balance")
    @CommandPermission("ctoken.balance")
    public void balance(@NotNull CommandSender sender, @NotNull @Default("me") OfflinePlayer target) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage("nincsen");
            return;
        }

        sender.sendMessage(FormatType.formatPrice(CToken.getDatabase().getBalance(target)));
    }

    @Subcommand("pay")
    @CommandPermission("ctoken.pay")
    public void pay(@NotNull Player player, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            player.sendMessage("nincsen");
            return;
        }

        if (value <= 0) {
            player.sendMessage("Invalid value");
            return;
        }

        if (CToken.getDatabase().getBalance(target) < value) {
            player.sendMessage("not enough token");
            return;
        }

        CToken.getDatabase().addToBalance(target, value);
        CToken.getDatabase().takeFromBalance(player, value);
        player.sendMessage("Added " + value + " to " + target.getName());
    }

    @Subcommand("reset")
    @CommandPermission("ctoken.reset")
    public void reset(@NotNull CommandSender sender, @NotNull String input) {
        if (input.equals("*")) {
            CToken.getDatabase().resetEveryone();
            sender.sendMessage("reset *");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage("nincsen");
            return;
        }

        CToken.getDatabase().resetBalance(target);
        sender.sendMessage("reset " + target.getName());
    }

    @Subcommand("set")
    @CommandPermission("ctoken.set")
    public void set(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage("nincsen");
            return;
        }

        if (value <= 0) {
            sender.sendMessage("Invalid value");
            return;
        }

        CToken.getDatabase().setBalance(target, value);
        sender.sendMessage("set " + value + " to " + target.getName());
    }

    @Subcommand("take")
    @CommandPermission("ctoken.take")
    public void take(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage("nincsen");
            return;
        }

        if (value <= 0) {
            sender.sendMessage("Invalid value");
            return;
        }

        CToken.getDatabase().takeFromBalance(target, value);
        sender.sendMessage("take " + value + " to " + target.getName());
    }

    @Subcommand("top")
    @CommandPermission("ctoken.top")
    public void top(@NotNull CommandSender sender, @Default("5") int value) {
        if (value <= 0) {
            sender.sendMessage("Invalid value");
            return;
        }

        sender.spigot().sendMessage(TokenTop.getTopDatabase(value));
    }
}
