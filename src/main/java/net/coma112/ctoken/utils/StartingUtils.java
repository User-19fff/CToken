package net.coma112.ctoken.utils;

import lombok.Getter;
import net.coma112.ctoken.CToken;
import net.coma112.ctoken.version.MinecraftVersion;
import net.coma112.ctoken.version.ServerVersionSupport;
import net.coma112.ctoken.version.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.coma112.ctoken.version.MinecraftVersion.determineVersion;

public final class StartingUtils {
    @Getter
    public static final Map<Long, String> basicFormatOverrides = new ConcurrentHashMap<>();
    private static boolean isSupported;

    public static void registerListenersAndCommands() {
        RegisterUtils.registerListeners();
        RegisterUtils.registerCommands();
    }

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(CToken.getInstance().getDataFolder(), resourcePath).exists())
            CToken.getInstance().saveResource(resourcePath, false);
    }

    public static void loadBasicFormatOverrides() {
        getBasicFormatOverrides().clear();

        ConfigurationSection section = CToken.getInstance().getConfiguration().getSection("formatting.basic");

        if (section == null) return;

        section.getKeys(false)
                .stream()
                .filter(key -> section.getString(key) != null)
                .forEach(key -> {
            getBasicFormatOverrides().put(Long.parseLong(key), section.getString(key));
        });
    }

    public static void checkVM() {
        int vmVersion = getVMVersion();
        if (vmVersion < 11) {
            Bukkit.getPluginManager().disablePlugin(CToken.getInstance());
            return;
        }

        if (!isSupported) {
            TokenLogger.error("This version of CToken is not supported on this server version.");
            TokenLogger.error("Please consider updating your server version to a newer version.");
            Bukkit.getPluginManager().disablePlugin(CToken.getInstance());
        }
    }

    public static void checkVersion() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception ignored) {
            isSupported = false;
            return;
        }

        try {
            String bukkitVersion = Bukkit.getVersion();
            Pattern pattern = Pattern.compile("\\(MC: (\\d+)\\.(\\d+)(?:\\.(\\d+))?\\)");
            Matcher matcher = pattern.matcher(bukkitVersion);

            if (matcher.find()) {
                int majorVersion = Integer.parseInt(matcher.group(1));
                int minorVersion = Integer.parseInt(matcher.group(2));
                int patchVersion = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                MinecraftVersion version = determineVersion(majorVersion, minorVersion, patchVersion);

                if (version == MinecraftVersion.UNKNOWN) {
                    isSupported = false;
                    return;
                }

                VersionSupport support = new VersionSupport(CToken.getInstance() ,version);
                ServerVersionSupport nms = support.getVersionSupport();
                isSupported = nms != null;

            } else {
                isSupported = false;
            }
        } catch (Exception exception) {
            isSupported = false;
        }
    }

    static int getVMVersion() {
        String javaVersion = System.getProperty("java.version");
        Matcher matcher = Pattern.compile("(?:1\\.)?(\\d+)").matcher(javaVersion);

        if (!matcher.find()) return -1;

        String version = matcher.group(1);

        try {
            return Integer.parseInt(version);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
