package net.coma112.ctoken.enums.keys;

import net.coma112.ctoken.item.ItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public enum ItemKeys {
    TOGGLE_PAY_ITEM("menu.toggle-pay"),
    FILLER_ITEM("filler-item"),
    SET_MINIMUM_PAY_ITEM("menu.minimum-pay");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path);
    }

    public ItemStack getItem(@NotNull String find, @NotNull String replacement) {
        ItemStack item = getItem();

        replaceItemMeta(item, find, replacement);
        return item;
    }

    public void getItem(@NotNull String find, @NotNull String replacement, @NotNull Inventory inventory) {
        inventory.setItem(ItemFactory.getSlot(path), getItem(find, replacement));
    }

    private void replaceItemMeta(@NotNull ItemStack item, @NotNull String find, @NotNull String replacement) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();

                displayName = displayName.replace(find, replacement);
                meta.setDisplayName(displayName);
            }

            if (meta.hasLore()) {
                List<String> lore = meta.getLore();

                if (lore != null) {
                    lore.replaceAll(string -> string.replace(find, replacement));
                    meta.setLore(lore);
                }
            }

            item.setItemMeta(meta);
        }
    }
}
