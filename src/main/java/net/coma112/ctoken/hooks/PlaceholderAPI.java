package net.coma112.ctoken.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.BadgeType;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.ConfigKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class PlaceholderAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "ct";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Coma112 (Vulcan Studio)";
    }

    @Override
    public @NotNull String getVersion() {
        return CToken.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
        if (params.startsWith("top_")) {
            try {
                int pos = Integer.parseInt(params.split("_")[1]);

                if (CToken.getDatabase().getTopPlayer(pos) != null) return CToken.getDatabase().getTopPlayer(pos);
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        if (params.startsWith("topbal_")) {
            try {
                int pos = Integer.parseInt(params.split("_")[1]);

                if (CToken.getDatabase().getTopBalance(pos) != 0) return String.valueOf(CToken.getDatabase().getTopBalance(pos));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        return switch (params) {
            case "balance" -> FormatType.formatPrice(CToken.getDatabase().getBalance(player));

            case "badge" -> {
                if (ConfigKeys.BADGES_ENABLED.getBoolean()) yield BadgeType.convertXPToBadge(CToken.getDatabase().getXP(player)).getDisplayName();
                yield "----";
            }

            case "xp" -> FormatType.formatPrice(CToken.getDatabase().getXP(player));

            default -> "";
        };
    }
}
