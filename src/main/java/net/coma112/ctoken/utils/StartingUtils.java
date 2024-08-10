package net.coma112.ctoken.utils;

import lombok.Getter;
import net.coma112.ctoken.CToken;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StartingUtils {
    @Getter
    public static final Map<Long, String> basicFormatOverrides = new ConcurrentHashMap<>();

    public static void registerListenersAndCommands() {
        RegisterUtils.registerEvents();
        RegisterUtils.registerCommands();
    }

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(CToken.getInstance().getDataFolder(), resourcePath).exists())
            CToken.getInstance().saveResource(resourcePath, false);
    }

    public static void loadBasicFormatOverrides() {
        ConfigurationSection section = CToken.getInstance().getConfiguration().getSection("formatting.basic");

        if (section != null) {
            section.getKeys(false).forEach(key -> {
                try {
                    long value = Long.parseLong(key);  // Change to long
                    String format = section.getString(key);

                    basicFormatOverrides.put(value, format);  // Map should also be long to String
                } catch (NumberFormatException exception) {
                    TokenLogger.error("Invalid formatting key: " + key);
                }
            });
        }
    }
}
