package net.coma112.ctoken.utils;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.commands.CommandToken;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public final class RegisterUtils {
    public static void registerListeners() {
        TokenLogger.info("### Registering listeners... ###");

        AtomicInteger count = new AtomicInteger();

        new Reflections("net.coma112.ctoken.listeners")
                .getSubTypesOf(Listener.class)
                .forEach(listenerClass -> {
                    try {
                        Bukkit.getServer().getPluginManager().registerEvents(listenerClass.getDeclaredConstructor().newInstance(), CToken.getInstance());
                        count.getAndIncrement();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                        TokenLogger.error(exception.getMessage());
                    }
                });

        TokenLogger.info("### Successfully registered {} listener. ###", count.get());
    }

    public static void registerCommands() {
        TokenLogger.info("### Registering commands... ###");

        BukkitCommandHandler handler = BukkitCommandHandler.create(CToken.getInstance());

        handler.register(new CommandToken());
        TokenLogger.info("### Successfully registered {} command(s). ###", handler.getCommands().size());

        TokenLogger.info("### Registering exception handlers... ###");
        handler.registerExceptionHandler(SenderNotPlayerException.class, TokenUtils::handleSenderNotPlayerException);
        handler.registerExceptionHandler(InvalidNumberException.class, TokenUtils::handleInvalidNumberException);
        handler.registerExceptionHandler(NoPermissionException.class, TokenUtils::handleNoPermissionException);
        handler.registerExceptionHandler(MissingArgumentException.class, TokenUtils::handleMissingArgumentException);
        handler.registerExceptionHandler(InvalidPlayerException.class, TokenUtils::handleInvalidPlayerException);
        handler.registerBrigadier();
        TokenLogger.info("### Successfully registered exception handlers... ###");
    }
}
