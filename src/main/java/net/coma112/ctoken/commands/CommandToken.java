package net.coma112.ctoken.commands;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.api.events.BalanceAddAllEvent;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.MessageKeys;
import net.coma112.ctoken.hooks.Webhook;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

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
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("add")
    @CommandPermission("ctoken.add")
    public void add(@NotNull CommandSender sender, @NotNull String input, int value) throws IOException, URISyntaxException {
        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        if (input.equals("*")) {
            CToken.getDatabase().addToEveryoneBalance(value);
            sender.sendMessage(MessageKeys.ADD_EVERYONE_SENDER
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            Bukkit.getServer().getPluginManager().callEvent(new BalanceAddAllEvent(value));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        CToken.getDatabase().addToBalance(target, value);
        sender.sendMessage(MessageKeys.ADD_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", input));
    }

    @Subcommand("balance")
    @CommandPermission("ctoken.balance")
    public void balance(@NotNull CommandSender sender, @NotNull @Default("me") OfflinePlayer target) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        sender.sendMessage(MessageKeys.BALANCE
                .getMessage()
                .replace("{target}", Objects.requireNonNull(target.getName()))
                .replace("{balance}", FormatType.format(CToken.getDatabase().getBalance(target))));
    }

    @Subcommand("pay")
    @CommandPermission("ctoken.pay")
    public void pay(@NotNull Player player, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            player.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        if (value <= 0) {
            player.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        if (CToken.getDatabase().getBalance(target) < value) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return;
        }

        CToken.getDatabase().addToBalance(target, value);
        CToken.getDatabase().takeFromBalance(player, value);
        player.sendMessage(MessageKeys.PAY_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.PAY_TARGET
                .getMessage()
                .replace("{player}", player.getName())
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("reset")
    @CommandPermission("ctoken.reset")
    public void reset(@NotNull CommandSender sender, @NotNull String input) throws IOException, URISyntaxException {
        if (input.equals("*")) {
            CToken.getDatabase().resetEveryone();
            sender.sendMessage(MessageKeys.RESET_EVERYONE_SENDER.getMessage());
            Webhook.sendWebhookFromString("webhook.balance-reset-all-embed", null);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        CToken.getDatabase().resetBalance(target);
        sender.sendMessage(MessageKeys.RESET_SENDER
                .getMessage()
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.RESET_TARGET.getMessage());
    }

    @Subcommand("set")
    @CommandPermission("ctoken.set")
    public void set(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        CToken.getDatabase().setBalance(target, value);
        sender.sendMessage(MessageKeys.SET_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.SET_TARGET
                .getMessage()
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("take")
    @CommandPermission("ctoken.take")
    public void take(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        CToken.getDatabase().takeFromBalance(target, value);
        sender.sendMessage(MessageKeys.TAKE_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.TAKE_TARGET
                .getMessage()
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("top")
    @CommandPermission("ctoken.top")
    public void top(@NotNull CommandSender sender, @Default("5") int value) {
        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        sender.spigot().sendMessage(TokenTop.getTopDatabase(value));
    }
}
