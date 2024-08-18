package net.coma112.ctoken.item;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.processor.MessageProcessor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public interface ItemFactory {
    static ItemFactory create(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    static ItemFactory create(@NotNull Material material, int count) {
        return new ItemBuilder(material, count);
    }

    static ItemFactory create(@NotNull Material material, int count, short damage) {
        return new ItemBuilder(material, count, damage);
    }

    static ItemFactory create(@NotNull Material material, int count, short damage, byte data) {
        return new ItemBuilder(material, count, damage, data);
    }

    static ItemFactory create(ItemStack item) {
        return new ItemBuilder(item);
    }

    ItemFactory setType(@NotNull Material material);

    ItemFactory setCount(int newCount);

    ItemFactory setName(@NotNull String name);

    void addEnchantment(@NotNull Enchantment enchantment, int level);

    default ItemFactory addEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);

        return this;
    }
    ItemBuilder setCustomModelData(int customModelData);

    ItemBuilder addLore(@NotNull String... lore);

    ItemFactory setUnbreakable();

    default void addFlag(@NotNull ItemFlag... flags) {
        Arrays
                .stream(flags)
                .forEach(this::addFlag);
    }

    default ItemFactory setLore(@NotNull String... lore) {
        Arrays
                .stream(lore)
                .forEach(this::addLore);
        return this;
    }

    ItemFactory removeLore(int line);

    ItemStack finish();

    boolean isFinished();

    static int getSlot(@NotNull String path) {
        ConfigurationSection section = CToken.getInstance().getConfiguration().getSection(path);

        if (section == null) return 0;

        return section.getInt("slot", 0);
    }

    static ItemStack createItemFromString(@NotNull String path) {
        ConfigurationSection section = CToken.getInstance().getConfiguration().getSection(path);

        Material material = Material.valueOf(Objects.requireNonNull(section).getString("material"));
        int amount = section.getInt("amount", 1);
        String name = section.getString("name");
        String[] loreArray = section.getStringList("lore").toArray(new String[0]);
        int customModelData = section.getInt("custom-model-data", 0);

        IntStream.range(0, loreArray.length).forEach(i -> loreArray[i] = MessageProcessor.process(loreArray[i]));

        return ItemFactory.create(material, amount)
                .setName(Objects.requireNonNull(name))
                .addLore(loreArray)
                .setCustomModelData(customModelData)
                .finish();
    }
}
