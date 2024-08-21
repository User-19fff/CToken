package net.coma112.ctoken.commands;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.MessageKeys;
import net.coma112.ctoken.events.BalanceAddAllEvent;
import net.coma112.ctoken.hooks.Webhook;
import net.coma112.ctoken.manager.TokenTop;
import net.coma112.ctoken.menu.menus.SettingsMenu;
import net.coma112.ctoken.utils.MenuUtils;
import net.coma112.ctoken.utils.StartingUtils;
import net.coma112.ctoken.utils.TokenUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.coma112.ctoken.utils.TokenUtils.*;

@Command({"ctoken", "token"})
public class CommandToken {
    @DefaultFor({"~", "~ help"})
    @CommandPermission("ctoken.help")
    public void help(@NotNull CommandSender sender) {
        MessageKeys.HELP
                .getMessages()
                .forEach(sender::sendMessage);
    }

    @Subcommand("reload")
    @CommandPermission("ctoken.reload")
    public void reload(@NotNull CommandSender sender) {
        CToken.getInstance().getLanguage().reload();
        CToken.getInstance().getConfiguration().reload();
        StartingUtils.loadBasicFormatOverrides();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("add")
    @CommandPermission("ctoken.add")
    @Usage("/ctoken add (input) (value)")
    public void add(@NotNull CommandSender sender, @NotNull String input, int value) {
        if (!handleInvalidValue(sender, value)) return;

        if (input.equals("*")) {
            CToken.getDatabase().addToEveryoneBalance(value);
            sender.sendMessage(MessageKeys.ADD_EVERYONE_SENDER
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            Bukkit.getServer().getPluginManager().callEvent(new BalanceAddAllEvent(value));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!handleNonTarget(sender, target)) return;
        if (!handleMaximumBalance(sender, target, value)) return;

        CToken.getDatabase().addToBalance(target, value);
        sender.sendMessage(MessageKeys.ADD_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", input));
    }

    @Subcommand("balance")
    @CommandPermission("ctoken.balance")
    public void balance(@NotNull CommandSender sender, @NotNull @Default("me") OfflinePlayer target) {
        if (!handleNonTarget(sender, target)) return;

        sender.sendMessage(MessageKeys.BALANCE
                .getMessage()
                .replace("{target}", Objects.requireNonNull(target.getName()))
                .replace("{balance}", FormatType.format(CToken.getDatabase().getBalance(target))));
    }

    @Subcommand("pay")
    @CommandPermission("ctoken.pay")
    @Usage("/ctoken pay (target) (value)")
    public void pay(@NotNull Player player, @NotNull OfflinePlayer target, int value) {
        if (!handleNonTarget(player, target)) return;
        if (!handleInvalidValue(player, value)) return;

        if (CToken.getDatabase().getBalance(target) < value) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return;
        }

        if (!CToken.getDatabase().getPayStatus(target)) {
            player.sendMessage(MessageKeys.PAYMENT_DISABLED
                    .getMessage()
                    .replace("{target}", Objects.requireNonNull(target.getName())));
            return;
        }

        if (value < CToken.getDatabase().getMinimumPay(target)) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_PAYMENT
                    .getMessage()
                    .replace("{value}", FormatType.format(CToken.getDatabase().getMinimumPay(target)))
                    .replace("{target}", Objects.requireNonNull(target.getName())));
            return;
        }

        if (!handleMaximumBalance(player, target, value)) return;

        CToken.getDatabase().addToBalance(target, value);
        CToken.getDatabase().takeFromBalance(player, value);
        player.sendMessage(MessageKeys.PAY_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        sendMessageToOfflinePlayer(target, MessageKeys.PAY_TARGET
                .getMessage()
                .replace("{player}", player.getName())
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("reset")
    @CommandPermission("ctoken.reset")
    @Usage("/ctoken reset (target or '*')")
    public void reset(@NotNull CommandSender sender, @NotNull String input) throws IOException, URISyntaxException {
        if (input.equals("*")) {
            CToken.getDatabase().resetEveryone();
            sender.sendMessage(MessageKeys.RESET_EVERYONE_SENDER.getMessage());
            Webhook.sendWebhookFromString("webhook.balance-reset-all-embed", null);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!handleNonTarget(sender, target)) return;

        CToken.getDatabase().resetBalance(target);
        sender.sendMessage(MessageKeys.RESET_SENDER
                .getMessage()
                .replace("{target}", Objects.requireNonNull(target.getName())));
        sendMessageToOfflinePlayer(target, MessageKeys.RESET_TARGET.getMessage());
    }

    @Subcommand("set")
    @CommandPermission("ctoken.set")
    @Usage("/ctoken set (target) (value)")
    @AutoComplete("@players")
    public void set(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!handleNonTarget(sender, target)) return;
        if (!handleNullableValue(sender, value)) return;
        if (!handleMaximumBalance(sender, target, value)) return;

        CToken.getDatabase().setBalance(target, value);
        sender.sendMessage(MessageKeys.SET_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        sendMessageToOfflinePlayer(target, MessageKeys.SET_TARGET
                .getMessage()
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("take")
    @CommandPermission("ctoken.take")
    @Usage("/ctoken add (input) (value)")
    public void take(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!handleNonTarget(sender, target)) return;
        if (!handleInvalidValue(sender, value)) return;

        CToken.getDatabase().takeFromBalance(target, value);
        sender.sendMessage(MessageKeys.TAKE_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        sendMessageToOfflinePlayer(target, MessageKeys.TAKE_TARGET
                .getMessage()
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("top")
    @CommandPermission("ctoken.top")
    public void top(@NotNull CommandSender sender, @Default("5") int value) {
        if (!handleInvalidValue(sender, value)) return;

        sender.sendMessage(TokenTop.getTopDatabase(value));
    }

    @Subcommand("settings")
    @CommandPermission("ctoken.settings")
    public void settings(@NotNull Player player) {
        new SettingsMenu(MenuUtils.getMenuUtils(player)).open();
    }

    @Subcommand("worth")
    @CommandPermission("ctoken.worth")
    public void inventory(@NotNull Player player) {
        Map<String, Integer> prices = Objects.requireNonNull(CToken.getInstance().getConfiguration().getSection("prices"))
                .getValues(false)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (Integer) entry.getValue()));

        AtomicInteger totalValue = new AtomicInteger(0);
        List<String> inventoryLines = Collections.synchronizedList(new ArrayList<>());

        inventoryLines.add("\n \n" + MessageKeys.WORTH_HEADER.getMessage() + "\n \n");

        Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> prices.containsKey(item.getType().name()))
                .forEach(item -> {
                    int itemTotalValue = prices.get(item.getType().name()) * item.getAmount();

                    totalValue.addAndGet(itemTotalValue);
                    inventoryLines.add(
                            MessageKeys.WORTH_ITEM.getMessage()
                                    .replace("{item}", item.getType().name().replace("_", " "))
                                    .replace("{amount}", String.valueOf(item.getAmount()))
                                    .replace("{value}", FormatType.format(itemTotalValue)) + "\n \n"
                    );
                });

        if (totalValue.get() > 0) {
            inventoryLines.add(MessageKeys.WORTH_TOTAL
                    .getMessage()
                    .replace("{total}", FormatType.format(totalValue.get())) + "\n \n");

            player.sendMessage(String.join("", inventoryLines));
        }

        player.sendMessage(MessageKeys.NO_VALUE.getMessage());
    }

    @Subcommand("sell")
    @CommandPermission("ctoken.sell")
    public void sell(@NotNull Player player, @Default("*") String itemName) {
        Map<String, Integer> prices = Objects.requireNonNull(CToken.getInstance().getConfiguration().getSection("prices"))
                .getValues(false)
                .entrySet()
                .stream()
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, entry -> (Integer) entry.getValue()));

        AtomicInteger totalValue = new AtomicInteger(0);
        AtomicInteger itemsSold = new AtomicInteger(0);

        Predicate<ItemStack> itemFilter = item -> item != null && (itemName.equals("*") || item.getType().name().equalsIgnoreCase(itemName)) && prices.containsKey(item.getType().name());

        Arrays.stream(player.getInventory().getContents())
                .filter(itemFilter)
                .forEach(item -> {
                    totalValue.addAndGet(prices.get(item.getType().name()) * item.getAmount());
                    itemsSold.addAndGet(item.getAmount());
                    player.getInventory().remove(item);
                });

        if (totalValue.get() <= 0) {
            player.sendMessage(MessageKeys.NO_VALUE.getMessage());
            return;
        }

        if (!handleMaximumBalanceOnSell(player, totalValue.get())) return;

        sendSellMessage(player, itemName, itemsSold.get(), totalValue.get());
        CToken.getDatabase().addToBalance(player, totalValue.get());
    }
}
