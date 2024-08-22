package net.coma112.ctoken.enums;

import net.coma112.ctoken.utils.TokenLogger;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public enum VersionType {
    UNKNOWN,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_19_R4,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3,
    v1_20_R6,
    v1_21_R1,
    v1_21_R2;

    private static VersionType serverVersion;

    static {
        String bukkitVersion = Bukkit.getVersion();
        Pattern pattern = Pattern.compile("\\(MC: (\\d+)\\.(\\d+)(?:\\.(\\d+))?\\)");
        Matcher matcher = pattern.matcher(bukkitVersion);

        if (matcher.find()) {
            try {
                int major = Integer.parseInt(matcher.group(1));
                int minor = Integer.parseInt(matcher.group(2));
                int patch = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;

                serverVersion = determineVersion(major, minor, patch);
            } catch (NumberFormatException exception) {
                serverVersion = UNKNOWN;
            }
        } else {
            serverVersion = UNKNOWN;
            TokenLogger.error("### Could not determine the server version from Bukkit version string: {} ###", bukkitVersion);
        }
    }

    public static VersionType determineVersion(int major, int minor, int patch) {
        if (major == 1) {
            return switch (minor) {
                case 18 -> switch (patch) {
                    case 1 -> v1_18_R1;
                    case 2 -> v1_18_R2;
                    default -> UNKNOWN;
                };

                case 19 -> switch (patch) {
                    case 1 -> v1_19_R1;
                    case 2 -> v1_19_R2;
                    case 3 -> v1_19_R3;
                    case 4 -> v1_19_R4;
                    default -> UNKNOWN;
                };

                case 20 -> switch (patch) {
                    case 1 -> v1_20_R1;
                    case 2 -> v1_20_R2;
                    case 4 -> v1_20_R3;
                    case 6 -> v1_20_R6;
                    default -> UNKNOWN;
                };

                case 21 -> switch (patch) {
                    case 1 -> v1_21_R1;
                    case 2 -> v1_21_R2;
                    default -> UNKNOWN;
                };

                default -> UNKNOWN;
            };
        }
        return UNKNOWN;
    }

    public static VersionType getCurrentVersion() {
        TokenLogger.info("Current Minecraft version: " + serverVersion);
        return serverVersion;
    }

    public static boolean isServerVersion(@NotNull VersionType version) {
        return serverVersion == version;
    }

    public static boolean isServerVersion(@NotNull VersionType... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public static boolean isServerVersionAbove(@NotNull VersionType version) {
        return serverVersion.ordinal() > version.ordinal();
    }

    public static boolean isServerVersionAtLeast(@NotNull VersionType version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public static boolean isServerVersionAtOrBelow(@NotNull VersionType version) {
        return serverVersion.ordinal() <= version.ordinal();
    }

    public static boolean isServerVersionBelow(@NotNull VersionType version) {
        return serverVersion.ordinal() < version.ordinal();
    }

    public boolean isLessThan(@NotNull VersionType other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isAtOrBelow(@NotNull VersionType other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isGreaterThan(@NotNull VersionType other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isAtLeast(@NotNull VersionType other) {
        return this.ordinal() >= other.ordinal();
    }
}
