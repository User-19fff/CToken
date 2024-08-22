package net.coma112.ctoken.listeners;

import net.coma112.ctoken.menu.Menu;
import net.coma112.ctoken.menu.menus.SettingsMenu;
import net.coma112.ctoken.utils.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu) {
            event.setCancelled(true);
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        SettingsMenu menu = new SettingsMenu(MenuUtils.getMenuUtils(player));

        if (event.getInventory().equals(menu.getInventory())) menu.close();
    }
}
