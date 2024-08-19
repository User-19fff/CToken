package net.coma112.ctoken.utils;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.MessageKeys;
import net.coma112.ctoken.hooks.Webhook;
import net.coma112.ctoken.interfaces.PlaceholderProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

import java.io.IOException;
import java.net.URISyntaxException;

@SuppressWarnings("all")
public final class TokenUtils {
    public static void handleSenderNotPlayerException(@NotNull CommandActor actor, @NotNull SenderNotPlayerException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.PLAYER_REQUIRED.getMessage());
    }

    public static void handleInvalidNumberException(@NotNull CommandActor actor, @NotNull InvalidNumberException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.INVALID_NUMBER.getMessage());
    }

    public static void handleNoPermissionException(@NotNull CommandActor actor, @NotNull NoPermissionException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.NO_PERMISSION.getMessage());
    }

    public static void handleMissingArgumentException(@NotNull CommandActor actor, @NotNull MissingArgumentException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.MISSING_ARGUMENT
                .getMessage()
                .replace("{usage}", context.getCommand().getUsage()));
    }

    public static void handleNonTarget(@NotNull CommandSender sender, @NotNull OfflinePlayer target) {
        if (!CToken.getDatabase().exists(target)) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }
    }

    public static void handleNonTarget(@NotNull Player player, @NotNull OfflinePlayer target) {
        if (!CToken.getDatabase().exists(target)) {
            player.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }
    }

    public static void handleInvalidValue(@NotNull Player player, int value) {
        if (value <= 0) {
            player.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }
    }

    public static void handleInvalidValue(@NotNull CommandSender sender, int value) {
        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }
    }

    public static void handleNullableValue(@NotNull CommandSender sender, int value) {
        if (value < 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }
    }

    public static void handleEvent(@NotNull String webhookKey, @NotNull PlaceholderProvider event) {
        try {
            Webhook.sendWebhookFromString(webhookKey, event);
        } catch (IOException | URISyntaxException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    public static void sendSellMessage(@NotNull Player player, @NotNull String itemName, int itemsSold, int totalValue) {
        String message;

        if (itemName.equals("*")) {
            message = MessageKeys.SOLD_ALL
                    .getMessage()
                    .replace("{value}", FormatType.format(totalValue));
        } else {
            message = MessageKeys.SOLD_ONE
                    .getMessage()
                    .replace("{amount}", String.valueOf(itemsSold))
                    .replace("{material}", itemName)
                    .replace("{value}", FormatType.format(totalValue));
        }

        player.sendMessage(message);
    }

    private static void sendMessage(@NotNull CommandActor actor, @NotNull String message) {
        actor.as(BukkitActor.class).getSender().sendMessage(message);
    }
}
