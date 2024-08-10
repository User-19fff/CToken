package net.coma112.ctoken.config;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.utils.ConfigUtils;

public class Config extends ConfigUtils {
    public Config() {
        super(CToken.getInstance().getDataFolder().getPath(), "config");
        save();
    }
}
