package net.coma112.ctoken.menu;

import net.coma112.ctoken.enums.keys.ItemKeys;
import net.coma112.ctoken.processor.MessageProcessor;
import net.coma112.ctoken.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public abstract class Menu implements InventoryHolder {

    protected MenuUtils menuUtils;
    protected Inventory inventory;

    public Menu(@NotNull MenuUtils menuUtils) {
        this.menuUtils = menuUtils;
    }

    public abstract void handleMenu(final InventoryClickEvent event);
    public abstract void setMenuItems();

    public abstract String getMenuName();

    public abstract int getSlots();
    public abstract int getMenuTick();
    public abstract boolean enableFillerItem();


    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), MessageProcessor.process(getMenuName()));

        this.setMenuItems();

        menuUtils.getOwner().openInventory(inventory);
        new MenuUpdater(this).start(getMenuTick());
    }

    public void setFillerItem() {
        if (!enableFillerItem()) return;

        IntStream.range(0, getSlots()).forEach(index -> {
            if (inventory.getItem(index) == null) inventory.setItem(index, ItemKeys.FILLER_ITEM.getItem());
        });
    }

    public void close() {
        MenuUpdater menuUpdater = new MenuUpdater(this);
        menuUpdater.stop();
        inventory = null;
    }

    public void updateMenuItems() {
        if (inventory != null) {
            setMenuItems();
            menuUtils.getOwner().updateInventory();
        }
    }
}