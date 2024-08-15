package net.coma112.ctoken.utils;

import net.coma112.ctoken.processor.MessageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

// This is NOT a token logger!! They're just the logging methods named after the plugin's name without the 'C'.

public final class TokenLogger {
    static final Logger logger = LogManager.getLogger("CToken");

    public static void info(@NotNull String msg, @NotNull Object... objs) {
        logger.info(msg, objs);
    }

    public static void warn(@NotNull String msg, @NotNull Object... objs) {
        logger.warn(msg, objs);
    }

    public static void error(@NotNull String msg, @NotNull Object... objs) {
        logger.error(msg, objs);
    }

    public static void colored(@NotNull String msg, @NotNull Object... objs) {
        Bukkit.getConsoleSender().sendMessage(MessageProcessor.process(new ParameterizedMessage(msg, objs).getFormattedMessage()));
    }
}
