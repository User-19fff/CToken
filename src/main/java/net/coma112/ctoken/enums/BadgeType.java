package net.coma112.ctoken.enums;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.keys.ConfigKeys;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public enum BadgeType {
    NOVICE,
    BEGINNER,
    COMPETENCE,
    PROFICIENT,
    EXPERT;

    private static final TreeMap<Integer, BadgeType> thresholds = new TreeMap<>();

    static {
        if (ConfigKeys.BADGES_ENABLED.getBoolean()) {
            YamlConfiguration config = CToken.getInstance().getConfiguration().getYml();

            thresholds.put(config.getInt("features.badges.limit.novice", 1000), NOVICE);
            thresholds.put(config.getInt("features.badges.limit.beginner", 2000), BEGINNER);
            thresholds.put(config.getInt("features.badges.limit.competence", 3000), COMPETENCE);
            thresholds.put(config.getInt("features.badges.limit.proficient", 4000), PROFICIENT);
            thresholds.put(config.getInt("features.badges.limit.expert", 5000), EXPERT);
        }
    }

    public static @NotNull BadgeType convertXPToBadge(int xp) {
        Map.Entry<Integer, BadgeType> entry = thresholds.floorEntry(xp);

        if (entry != null) return entry.getValue();
        else return NOVICE;
    }

    public @NotNull String getDisplayName() {
        return Optional
                .ofNullable(CToken.getInstance().getConfig().getString("features.badges." + this.name().toLowerCase()))
                .orElse(this.name());
    }
}
