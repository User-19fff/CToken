package net.coma112.ctoken.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.coma112.ctoken.processor.MessageProcessor;
import net.coma112.ctoken.utils.TokenLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

@SuppressWarnings("all")
public class ItemBuilder implements ItemFactory {
    private final ItemStack is;
    private final ItemMeta meta;
    private boolean finished = false;
    private String currentBase64 = "";

    public ItemBuilder(@NotNull ItemStack item) {
        is = item;
        meta = item.getItemMeta();
    }

    ItemBuilder(@NotNull Material type) {
        this(type, 1);
    }

    public ItemBuilder(@NotNull Material type, @Range(from = 0, to = 64) int amount) {
        this(type, amount, (short) 0);
    }

    public ItemBuilder(@NotNull Material type, @Range(from = 0, to = 64) int amount, short damage) {
        this(type, amount, damage, null);
    }

    public ItemBuilder(@NotNull Material type, @Range(from = 0, to = 64) int amount, short damage, @Nullable Byte data) {
        is = new ItemStack(type, amount, damage, data);
        meta = is.getItemMeta();
    }

    @Override
    public ItemBuilder setCustomModelData(@NotNull int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    @Override
    public ItemBuilder setType(@NotNull Material material) {
        is.setType(material);
        return this;
    }

    @Override
    public ItemBuilder setHead(@NotNull String base64) {
        SkullMeta headMeta = (SkullMeta) is.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field field = headMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            TokenLogger.error(exception.getMessage());
        }

        is.setItemMeta(headMeta);

        // Log only if the texture has actually changed
        if (!base64.equals(currentBase64)) {
            TokenLogger.info("Skull texture set: " + base64);
            currentBase64 = base64;  // Update the stored texture
            return this;
        }

        return this;
    }

    @Override
    public ItemBuilder setCount(@Range(from = 0, to = 64) int newCount) {
        is.setAmount(newCount);
        return this;
    }

    @Override
    public ItemBuilder setName(@NotNull String name) {
        meta.setDisplayName(MessageProcessor.process(name));
        return this;
    }

    @Override
    public void addEnchantment(@NotNull Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
    }

    @Override
    public ItemBuilder addLore(@NotNull String... lores) {
        List<String> loreList = Arrays.asList(lores);
        List<String> currentLores = meta.getLore();
        currentLores = currentLores == null ? new ArrayList<>() : currentLores;
        currentLores.addAll(loreList);
        meta.setLore(currentLores);

        return this;
    }

    @Override
    public ItemBuilder setUnbreakable() {
        meta.setUnbreakable(true);

        return this;
    }

    public ItemBuilder addFlag(@NotNull ItemFlag flag) {
        meta.addItemFlags(flag);

        return this;
    }

    @Override
    public ItemBuilder removeLore(int line) {
        List<String> lores = meta.getLore();
        lores = lores == null ? new ArrayList<>() : lores;

        lores.remove(Math.min(line, lores.size()));
        meta.setLore(lores);

        return this;
    }

    @Override
    public ItemStack finish() {
        is.setItemMeta(meta);

        finished = true;
        return is;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}