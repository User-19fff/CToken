package net.coma112.ctoken.listener;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.events.BalanceChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GlobalListener implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        CToken.getDatabase().createPlayer(event.getPlayer());
    }

    @EventHandler
    public void onBalanceChange(final BalanceChangeEvent event) {
        CToken.getDatabase().handleBalanceChangeEvent(event);
    }
}
