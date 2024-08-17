package net.coma112.ctoken.menu.menus;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.keys.ConfigKeys;
import net.coma112.ctoken.enums.keys.ItemKeys;
import net.coma112.ctoken.enums.keys.MessageKeys;
import net.coma112.ctoken.menu.Menu;
import net.coma112.ctoken.utils.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class SettingsMenu extends Menu {
    public SettingsMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.MENU_SIZE.getInt();
    }

    @Override
    public int getMenuTick() {
        return ConfigKeys.MENU_UPDATE_TICK.getInt();
    }

    @Override
    public boolean enableFillerItem() {
        return ConfigKeys.MENU_FILLER_ITEM.getBoolean();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        setFillerItem();

        inventory.setItem(ConfigKeys.TOGGLE_PAY_SLOT.getInt(), ItemKeys.TOGGLE_PAY_ITEM.getItem("{status}", CToken.getDatabase().getPayStatus(menuUtils.getOwner()) ? ConfigKeys.ENABLED.getString() : ConfigKeys.DISABLED.getString()));
        inventory.setItem(ConfigKeys.MINIMUM_PAY_SLOT.getInt(), ItemKeys.SET_MINIMUM_PAY_ITEM.getItem("{value}", String.valueOf(CToken.getDatabase().getMinimumPay(menuUtils.getOwner()))));
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        int slot = event.getSlot();

        if (slot == ConfigKeys.TOGGLE_PAY_SLOT.getInt()) {
            CToken.getDatabase().changePayStatus(player);
            updateMenuItems();
            return;
        }

        if (slot == ConfigKeys.MINIMUM_PAY_SLOT.getInt()) {
            player.closeInventory();
            player.sendMessage(MessageKeys.ENTER_NUMBER.getMessage());

            CToken.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onAsyncChat(final AsyncPlayerChatEvent chatEvent) {
                    if (!chatEvent.getPlayer().equals(player)) return;

                    String message = chatEvent.getMessage();

                    if (!message.matches("\\d+")) {
                        player.sendMessage(MessageKeys.INVALID_NUMBER.getMessage());
                        event.setCancelled(true);
                        return;
                    }

                    chatEvent.setCancelled(true);
                    CToken.getDatabase().changeMinimumPay(player, Integer.parseInt(message));
                    CToken.getInstance().getServer().getScheduler().runTask(CToken.getInstance(), () -> {
                        player.openInventory(SettingsMenu.this.getInventory());
                        updateMenuItems();
                    });

                    AsyncPlayerChatEvent.getHandlerList().unregister(this);
                }
            }, CToken.getInstance());
        }
    }
}
