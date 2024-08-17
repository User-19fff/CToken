package net.coma112.ctoken.listeners;

import net.coma112.ctoken.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.coma112.ctoken.utils.TokenUtils.handleEvent;

public class WebhookListener implements Listener {
    @EventHandler
    public void onTake(final BalanceTakeEvent event) {
        handleEvent("webhook.balance-take-embed", event);
    }

    @EventHandler
    public void onSet(final BalanceSetEvent event) {
        handleEvent("webhook.balance-set-embed", event);
    }

    @EventHandler
    public void onReset(final BalanceResetEvent event) {
        handleEvent("webhook.balance-reset-embed", event);
    }

    @EventHandler
    public void onAdd(final BalanceAddEvent event) {
        handleEvent("webhook.balance-add-embed", event);
    }

    @EventHandler
    public void onAddAll(final BalanceAddAllEvent event) {
        handleEvent("webhook.balance-add-all-embed", event);
    }
}
