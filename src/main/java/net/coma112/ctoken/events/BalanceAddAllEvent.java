package net.coma112.ctoken.events;

import lombok.Getter;
import net.coma112.ctoken.interfaces.PlaceholderProvider;
import net.coma112.ctoken.interfaces.RegisterableListener;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BalanceAddAllEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final int addedAmount;

    public BalanceAddAllEvent(int addedAmount) {
        this.addedAmount = addedAmount;
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

        placeholders.put("{addedAmount}", String.valueOf(addedAmount));

        return placeholders;
    }
}
