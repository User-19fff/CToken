package net.coma112.ctoken.enums.keys;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum ConfigKeys {
    LANGUAGE("language"),
    DATABASE("database.type"),
    FORMATTING_ENABLED("formatting.enabled"),
    FORMATTING_TYPE("formatting.type"),
    STARTING_BALANCE("starting-balance"),

    BADGES_ENABLED("features.badges.enabled"),
    BADGES_MULTIPLIER("features.badges.multiplier");


    private final String path;

    ConfigKeys(@NotNull final String path) {
        this.path = path;
    }

    public String getString() {
        return MessageProcessor.process(CToken.getInstance().getConfiguration().getString(path));
    }

    public boolean getBoolean() {
        return CToken.getInstance().getConfiguration().getBoolean(path);
    }

    public int getInt() {
        return CToken.getInstance().getConfiguration().getInt(path);
    }

    public double getDouble() {
        return CToken.getInstance().getConfiguration().getYml().getDouble(path);
    }
}
