package net.coma112.ctoken.listener;

import net.coma112.ctoken.events.*;
import net.coma112.ctoken.hooks.Webhook;
import net.coma112.ctoken.interfaces.RegisterableListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.net.URISyntaxException;

public class WebhookListener implements Listener, RegisterableListener {
    @EventHandler
    public void onTake(final BalanceTakeEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.balance-take-embed", event);
    }

    @EventHandler
    public void onSet(final BalanceSetEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.balance-set-embed", event);
    }

    @EventHandler
    public void onReset(final BalanceResetEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.balance-reset-embed", event);
    }

    @EventHandler
    public void onAdd(final BalanceAddEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.balance-add-embed", event);
    }

    @EventHandler
    public void onAddAll(final BalanceAddAllEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.balance-add-all-embed", event);
    }
}
