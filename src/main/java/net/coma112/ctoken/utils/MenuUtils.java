package net.coma112.ctoken.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class MenuUtils {
    private final Player owner;
    private static final Map<Player, MenuUtils> menuMap = new ConcurrentHashMap<>();

    public MenuUtils(@NotNull Player player) {
        this.owner = player;
    }

    public static MenuUtils getMenuUtils(@NotNull Player player) {
        return menuMap.computeIfAbsent(player, MenuUtils::new);
    }
}
