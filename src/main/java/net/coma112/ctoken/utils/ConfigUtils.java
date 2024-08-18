package net.coma112.ctoken.utils;

import lombok.Getter;
import lombok.Setter;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ConfigUtils {
    @Getter
    private YamlConfiguration yml;
    @Getter
    @Setter
    private String name;
    private File config;
    private YamlConfiguration defaultYml;

    public ConfigUtils(@NotNull String dir, @NotNull String name) {
        File file = new File(dir);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                TokenLogger.error("Failed to create directories: " + dir);
                return;
            }
        }

        this.config = new File(dir, name + ".yml");

        if (!config.exists()) {
            try {
                if (!config.createNewFile()) {
                    TokenLogger.error("Failed to create config file: " + config.getAbsolutePath());
                    return;
                }
            } catch (IOException exception) {
                TokenLogger.error("Error creating config file: " + exception.getMessage());
            }
        }

        this.yml = YamlConfiguration.loadConfiguration(config);
        this.name = name;

        try (InputStream defaultConfigStream = getClass().getClassLoader().getResourceAsStream(name + ".yml")) {

            if (defaultConfigStream == null) return;

            this.defaultYml = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));

            yml.options().copyDefaults(true);
            addMissingKeys();
            TokenLogger.info("Loaded " + name + ".yml");
        } catch (IOException exception) {
            TokenLogger.error("Error loading default config: " + exception.getMessage());
        }
    }

    public void reload() {
        this.yml = YamlConfiguration.loadConfiguration(config);

        addMissingKeys();
        save();
    }

    public void set(@NotNull String path, @NotNull Object value) {
        yml.set(path, value);
        save();
    }

    public void save() {
        try {
            yml.save(config);
        } catch (IOException exception) {
            TokenLogger.error("Error saving config file: " + exception.getMessage());
        }
    }

    public List<String> getList(@NotNull String path) {
        return yml.getStringList(path)
                .stream()
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

    private void addMissingKeys() {
        if (defaultYml == null) return;

        boolean changed = defaultYml.getKeys(true)
                .stream()
                .filter(key -> !yml.contains(key))
                .peek(key -> yml.set(key, defaultYml.get(key)))
                .findFirst()
                .isPresent();

        if (changed) save();
    }
}
