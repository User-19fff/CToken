package net.coma112.ctoken.utils;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.commands.CommandToken;
import net.coma112.ctoken.interfaces.RegisterableListener;
import net.coma112.ctoken.listener.JoinListener;
import net.coma112.ctoken.listener.WebhookListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public final class RegisterUtils {
    public static void registerListeners() {
        new Reflections("net.coma112.ctoken.listeners")
                .getSubTypesOf(RegisterableListener.class)
                .forEach(listenerClass -> {
            try {
                Bukkit.getServer().getPluginManager().registerEvents(listenerClass.getDeclaredConstructor().newInstance(), CToken.getInstance());
            } catch (Exception exception) {
                TokenLogger.error(exception.getMessage());
            }
        });
    }

    public static void registerCommands() {
        BukkitCommandHandler
                .create(CToken.getInstance())
                .register(new CommandToken());
    }
}
