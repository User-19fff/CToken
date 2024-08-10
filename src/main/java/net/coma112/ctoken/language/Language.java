package net.coma112.ctoken.language;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.utils.ConfigUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Language extends ConfigUtils {
    public Language(@NotNull String name) {
        super(CToken.getInstance().getDataFolder().getPath() + File.separator + "locales", name);
        save();
    }
}
