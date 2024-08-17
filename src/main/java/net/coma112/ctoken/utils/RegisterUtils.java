package net.coma112.ctoken.utils;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.commands.CommandToken;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

import java.lang.reflect.InvocationTargetException;

public final class RegisterUtils {
    public static void registerListeners() {
        new Reflections("net.coma112.ctoken.listeners")
                .getSubTypesOf(Listener.class)
                .forEach(listenerClass -> {
                    try {
                        Bukkit.getServer().getPluginManager().registerEvents(listenerClass.getDeclaredConstructor().newInstance(), CToken.getInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                        TokenLogger.error(exception.getMessage());
                    }
                });
    }

    public static void registerCommands() {
        BukkitCommandHandler handler = BukkitCommandHandler.create(CToken.getInstance());

        handler.register(new CommandToken());

        handler.registerExceptionHandler(SenderNotPlayerException.class, TokenUtils::handleSenderNotPlayerException);
        handler.registerExceptionHandler(InvalidNumberException.class, TokenUtils::handleInvalidNumberException);
        handler.registerExceptionHandler(NoPermissionException.class, TokenUtils::handleNoPermissionException);
        handler.registerExceptionHandler(MissingArgumentException.class, TokenUtils::handleMissingArgumentException);
        handler.registerBrigadier();
    }
}
