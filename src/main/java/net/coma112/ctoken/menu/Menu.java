package net.coma112.ctoken.menu;

import net.coma112.ctoken.enums.keys.ConfigKeys;
import net.coma112.ctoken.processor.MessageProcessor;
import net.coma112.ctoken.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public abstract class Menu implements InventoryHolder {

    protected MenuUtils menuUtils;
    protected Inventory inventory;

    public Menu(@NotNull MenuUtils menuUtils) {
        this.menuUtils = menuUtils;
    }

    @NotNull
    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(final InventoryClickEvent event);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), MessageProcessor.process(getMenuName()));

        setMenuItems();
        menuUtils.getOwner().openInventory(inventory);
        new MenuUpdater(this).start(ConfigKeys.MENU_UPDATE_TICK.getDouble() * 20);
    }

    public void updateMenuItems() {
        if (inventory != null) {
            inventory.clear();
            setMenuItems();
            menuUtils.getOwner().updateInventory();
        }
    }

    public void close() {
        new MenuUpdater(this).stop();

        inventory = null;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}