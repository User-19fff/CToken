package net.coma112.ctoken.version;

import lombok.Getter;
import net.coma112.ctoken.CToken;
import net.coma112.ctoken.interfaces.ServerVersionSupport;
import net.coma112.ctoken.utils.TokenLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.tokens.Token;

import java.lang.reflect.InvocationTargetException;

@Getter
public class VersionSupport {
    private final ServerVersionSupport versionSupport;

    public VersionSupport(@NotNull MinecraftVersion version) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (version == MinecraftVersion.UNKNOWN) TokenLogger.error("### VERSION NOT FOUND! ###");


        Class<?> clazz = Class.forName("net.coma112.ctoken.version.nms." + version.name() + ".ServerVersion");
        versionSupport = (ServerVersionSupport) clazz.getConstructor(Plugin.class).newInstance(CToken.getInstance());

        if (!versionSupport.isSupported()) {
            TokenLogger.warn("---   VERSION IS SUPPORTED BUT,   ---");
            TokenLogger.warn("The version you are using is badly");
            TokenLogger.warn("implemented. Many features won't work.");
            TokenLogger.warn("Please consider updating your server ");
            TokenLogger.warn("version to a newer version. (like 1.20_R1)");
            TokenLogger.warn("---   PLEASE READ THIS MESSAGE!   ---");
        }

        TokenLogger.info("### Version support for {} loaded! ###", version);
    }
}
