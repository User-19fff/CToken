package net.coma112.ctoken.listener;

import net.coma112.ctoken.CToken;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        CToken.getDatabase().createPlayer(event.getPlayer());
    }
}
