package net.coma112.ctoken.menu;

import net.coma112.ctoken.utils.MenuUtils;
import org.jetbrains.annotations.NotNull;

public abstract class PaginatedMenu extends Menu {
    public abstract void addMenuBorder();

    public PaginatedMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }
}
