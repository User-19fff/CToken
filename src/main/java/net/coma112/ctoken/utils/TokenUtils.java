package net.coma112.ctoken.utils;

import net.coma112.ctoken.enums.keys.MessageKeys;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

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

    private static void sendMessage(@NotNull CommandActor actor, @NotNull String message) {
        actor.as(BukkitActor.class).getSender().sendMessage(message);
    }
}
