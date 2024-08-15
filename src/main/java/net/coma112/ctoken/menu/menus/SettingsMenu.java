package net.coma112.ctoken.menu.menus;

import net.coma112.ctoken.enums.keys.ConfigKeys;
import net.coma112.ctoken.enums.keys.ItemKeys;
import net.coma112.ctoken.menu.Menu;
import net.coma112.ctoken.utils.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class SettingsMenu extends Menu {
    public SettingsMenu(@NotNull MenuUtils menuUtils) { super(menuUtils); }

    @Override
    public @NotNull String getMenuName() { return ConfigKeys.MENU_TITLE.getString(); }

    @Override
    public int getSlots() { return ConfigKeys.MENU_SIZE.getInt(); }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);
    }

    @Override
    public void setMenuItems() {
        ItemKeys.TOGGLE_PAY_ITEM.getItem(inventory);
        ItemKeys.SET_MINIMUM_PAY_ITEM.getItem(inventory);
    }
}
