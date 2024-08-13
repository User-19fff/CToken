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
        RegisterUtils.registerListeners();
        RegisterUtils.registerCommands();
    }

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(CToken.getInstance().getDataFolder(), resourcePath).exists())
            CToken.getInstance().saveResource(resourcePath, false);
    }

    public static void loadBasicFormatOverrides() {
        if (!basicFormatOverrides.isEmpty()) basicFormatOverrides.clear();

        ConfigurationSection section = CToken.getInstance().getConfiguration().getSection("formatting.basic");

        if (section == null) return;

        section.getKeys(false).forEach(key -> {
            try {
                basicFormatOverrides.put(Long.parseLong(key), section.getString(key));
            } catch (NumberFormatException exception) {
                TokenLogger.error("Invalid formatting key: " + key);
            }
        });
    }
}
