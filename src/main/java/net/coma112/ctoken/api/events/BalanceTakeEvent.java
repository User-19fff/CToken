package net.coma112.ctoken.api.events;

import lombok.Getter;
import net.coma112.ctoken.interfaces.PlaceholderProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BalanceTakeEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer player;
    private final int oldBalance;
    private final int takenAmount;

    public BalanceTakeEvent(@NotNull OfflinePlayer player, int oldBalance, int takenAmount) {
        this.player = player;
        this.oldBalance = oldBalance;
        this.takenAmount = takenAmount;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{player}", player.getName());
        placeholders.put("{takenAmount}", String.valueOf(takenAmount));
        placeholders.put("{oldBalance}", String.valueOf(oldBalance));

        return placeholders;
    }
}
