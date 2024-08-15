package net.coma112.ctoken.utils;

import lombok.Getter;
import net.coma112.ctoken.processor.MessageProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtils {
    @Getter
    private YamlConfiguration yml;
    @Getter
    private String name;
    private File config;
    private YamlConfiguration defaultYml;

    public ConfigUtils(@NotNull String dir, @NotNull String name) {
        File file = new File(dir);

        if (!file.exists()) {
            if (!file.mkdirs()) return;
        }

        config = new File(dir, name + ".yml");

        if (!config.exists()) {
            try {
                if (!config.createNewFile()) return;
            } catch (IOException exception) {
                TokenLogger.error(exception.getMessage());
            }
        }

        yml = YamlConfiguration.loadConfiguration(config);
        this.name = name;

        InputStream defaultConfigStream = getClass().getClassLoader().getResourceAsStream(name + ".yml");

        if (defaultConfigStream != null) {
            defaultYml = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
            yml.options().copyDefaults(true);
            addMissingKeys();
            TokenLogger.info("Loaded " + name + ".yml");
        }
    }

    public void reload() {
        yml = YamlConfiguration.loadConfiguration(config);
        addMissingKeys();
        save();
    }

    public void set(@NotNull String path, Object value) {
        yml.set(path, value);
        save();
    }

    public void save() {
        try {
            yml.save(config);
        } catch (IOException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    public List<String> getList(@NotNull String path) {
        return yml.getStringList(path)
                .stream()
                .map(MessageProcessor::process)
                .collect(Collectors.toList());
    }

    public List<String> getLoreList(@NotNull String path) {
        return getList(path).stream()
                .map(MessageProcessor::process)
                .collect(Collectors.toList());
    }

    public boolean getBoolean(@NotNull String path) {
        return yml.getBoolean(path);
    }

    public int getInt(@NotNull String path) {
        return yml.getInt(path);
    }

    public String getString(@NotNull String path) {
        return yml.getString(path);
    }

    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return yml.getConfigurationSection(path);
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    private void addMissingKeys() {
        if (defaultYml == null) return;

        boolean changed = defaultYml.getKeys(true)
                .stream()
                .anyMatch(key -> !yml.contains(key));

        if (changed) save();
    }
}

