package net.coma112.ctoken.utils;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.commands.CommandToken;
import net.coma112.ctoken.listener.GlobalListener;
import org.bukkit.event.Listener;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public final class RegisterUtils {
    public static void registerListeners() {
        getListenerClasses().forEach(clazz -> {
            try {
                CToken.getInstance().getServer().getPluginManager().registerEvents(clazz.getDeclaredConstructor().newInstance(), CToken.getInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                TokenLogger.error(exception.getMessage());
            }
        });
    }

    public static void registerCommands() {
        BukkitCommandHandler handler = BukkitCommandHandler.create(CToken.getInstance());

        handler.register(new CommandToken());
    }

    private static Set<Class<? extends Listener>> getListenerClasses() {
        Set<Class<? extends Listener>> listenerClasses = new HashSet<>();

        listenerClasses.add(GlobalListener.class);

        return listenerClasses;
    }
}
