package net.coma112.ctoken.listener;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.interfaces.RegisterableListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener, RegisterableListener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        CToken.getDatabase().createPlayer(event.getPlayer());
    }
}
