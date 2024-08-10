package net.coma112.ctoken.events;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public final class BalanceChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer player;
    private final int oldBalance;
    private final int newBalance;

    public BalanceChangeEvent(@NotNull final OfflinePlayer player, final int oldBalance, final int newBalance) {
        this.player = player;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
