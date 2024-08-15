package net.coma112.ctoken.enums.keys;

import net.coma112.ctoken.item.ItemFactory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public enum ItemKeys {
    TOGGLE_PAY_ITEM("menu.minimum-pay"),
    SET_MINIMUM_PAY_ITEM("menu.toggle-pay");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public void getItem(@NotNull Inventory inventory) {
        ItemFactory.createItemFromString(path, inventory);
    }
}
