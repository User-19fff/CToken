package net.coma112.ctoken.menu;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import net.coma112.ctoken.CToken;
import org.jetbrains.annotations.NotNull;

public class MenuUpdater {
    private final Menu menu;
    private MyScheduledTask task;
    private boolean running = true;

    public MenuUpdater(@NotNull Menu menu) {
        this.menu = menu;
    }

    public void run() {
        if (!running) {
            stop();
            return;
        }

        if (menu.getInventory().getViewers().contains(menu.menuUtils.getOwner())) menu.updateMenuItems();
        else stop();
    }

    public void start(int intervalTicks) {
        if (task != null) return;

        task = CToken.getInstance().getScheduler().runTaskTimer(this::run, intervalTicks, intervalTicks);
    }

    public void stop() {
        running = false;

        if (task == null) return;

        task.cancel();
        task = null;
    }
}
