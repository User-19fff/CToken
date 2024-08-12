package net.coma112.ctoken.commands;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.api.events.BalanceAddAllEvent;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.MessageKeys;
import net.coma112.ctoken.hooks.Webhook;
import net.coma112.ctoken.manager.TokenTop;
import net.coma112.ctoken.utils.StartingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Command({"ctoken", "token"})
@SuppressWarnings("deprecation")
public class CommandToken {
    @DefaultFor({"token", "ctoken"})
    public void defaultCommand(@NotNull CommandSender sender) {
        help(sender);
    }

    @Subcommand("help")
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
    public void add(@NotNull CommandSender sender, @NotNull String input, int value) throws IOException, URISyntaxException {
        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        if (input.equals("*")) {
            CToken.getDatabase().addToEveryoneBalance(value);
            sender.sendMessage(MessageKeys.ADD_EVERYONE_SENDER
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            Bukkit.getServer().getPluginManager().callEvent(new BalanceAddAllEvent(value));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        CToken.getDatabase().addToBalance(target, value);
        sender.sendMessage(MessageKeys.ADD_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", input));
    }

    @Subcommand("balance")
    @CommandPermission("ctoken.balance")
    public void balance(@NotNull CommandSender sender, @NotNull @Default("me") OfflinePlayer target) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        sender.sendMessage(MessageKeys.BALANCE
                .getMessage()
                .replace("{target}", Objects.requireNonNull(target.getName()))
                .replace("{balance}", FormatType.format(CToken.getDatabase().getBalance(target))));
    }

    @Subcommand("pay")
    @CommandPermission("ctoken.pay")
    public void pay(@NotNull Player player, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            player.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        if (value <= 0) {
            player.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        if (CToken.getDatabase().getBalance(target) < value) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return;
        }

        CToken.getDatabase().addToBalance(target, value);
        CToken.getDatabase().takeFromBalance(player, value);
        player.sendMessage(MessageKeys.PAY_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.PAY_TARGET
                .getMessage()
                .replace("{player}", player.getName())
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("reset")
    @CommandPermission("ctoken.reset")
    public void reset(@NotNull CommandSender sender, @NotNull String input) throws IOException, URISyntaxException {
        if (input.equals("*")) {
            CToken.getDatabase().resetEveryone();
            sender.sendMessage(MessageKeys.RESET_EVERYONE_SENDER.getMessage());
            Webhook.sendWebhookFromString("webhook.balance-reset-all-embed", null);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        CToken.getDatabase().resetBalance(target);
        sender.sendMessage(MessageKeys.RESET_SENDER
                .getMessage()
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.RESET_TARGET.getMessage());
    }

    @Subcommand("set")
    @CommandPermission("ctoken.set")
    public void set(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        if (value < 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        CToken.getDatabase().setBalance(target, value);
        sender.sendMessage(MessageKeys.SET_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.SET_TARGET
                .getMessage()
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("take")
    @CommandPermission("ctoken.take")
    public void take(@NotNull CommandSender sender, @NotNull OfflinePlayer target, int value) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(MessageKeys.TARGET_DONT_EXIST.getMessage());
            return;
        }

        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        CToken.getDatabase().takeFromBalance(target, value);
        sender.sendMessage(MessageKeys.TAKE_SENDER
                .getMessage()
                .replace("{value}", FormatType.format(value))
                .replace("{target}", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.TAKE_TARGET
                .getMessage()
                .replace("{value}", FormatType.format(value)));
    }

    @Subcommand("top")
    @CommandPermission("ctoken.top")
    public void top(@NotNull CommandSender sender, @Default("5") int value) {
        if (value <= 0) {
            sender.sendMessage(MessageKeys.INVALID_VALUE
                    .getMessage()
                    .replace("{value}", FormatType.format(value)));
            return;
        }

        sender.spigot().sendMessage(TokenTop.getTopDatabase(value));
    }

    @Subcommand("inventory")
    @CommandPermission("ctoken.inventory")
    public void inventory(@NotNull Player player) {
        Map<String, Integer> prices = new HashMap<>();

        Objects.requireNonNull(CToken.getInstance().getConfiguration().getSection("prices"))
                .getValues(false)
                .forEach((key, value) -> prices.put(key, (Integer) value));

        AtomicInteger totalValue = new AtomicInteger(0);
        StringBuilder inventoryList = new StringBuilder();
        String headerMessage = MessageKeys.WORTH_HEADER.getMessage();

        inventoryList
                .append(headerMessage)
                .append("\n");

        Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .forEach(item -> {
                    Material material = item.getType();
                    String materialName = material.name();

                    if (!prices.containsKey(materialName)) return;

                    int itemAmount = item.getAmount();
                    int itemTotalValue = prices.get(materialName) * itemAmount;

                    totalValue.addAndGet(itemTotalValue);

                    String itemMessage = MessageKeys.WORTH_ITEM.getMessage()
                            .replace("{item}", materialName.replace("_", " "))
                            .replace("{amount}", String.valueOf(itemAmount))
                            .replace("{value}", FormatType.format(itemTotalValue));

                    inventoryList
                            .append(itemMessage)
                            .append("\n");
                });

        if (totalValue.get() > 0) {
            inventoryList
                    .append("\n")
                    .append(MessageKeys.WORTH_TOTAL.getMessage()
                    .replace("{total}", FormatType.format(totalValue.get())));

            player.sendMessage(inventoryList.toString());

        } else player.sendMessage(MessageKeys.NO_VALUE.getMessage());
    }

    @Subcommand("sell")
    @CommandPermission("ctoken.sell")
    public void sell(@NotNull Player player, @Default("*") String itemName) {
        Map<String, Integer> prices = new HashMap<>();

        Objects.requireNonNull(CToken.getInstance().getConfiguration().getSection("prices"))
                .getValues(false)
                .forEach((key, value) -> prices.put(key, (Integer) value));

        AtomicInteger totalValue = new AtomicInteger(0);
        AtomicInteger itemsSold = new AtomicInteger(0);

        if (itemName.equals("*")) {
            Arrays.stream(player.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .forEach(item -> {
                Material material = item.getType();
                String materialName = material.name();

                if (!prices.containsKey(materialName)) return;

                int itemAmount = item.getAmount();

                totalValue.addAndGet(prices.get(materialName) * itemAmount);
                itemsSold.addAndGet(itemAmount);
                player.getInventory().remove(item);
            });

            player.sendMessage(MessageKeys.SOLD_ALL.getMessage()
                    .replace("{value}", FormatType.format(totalValue.get())));
        } else {
            Arrays.stream(player.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .forEach(item -> {
                Material material = item.getType();
                String materialName = material.name();

                if (!materialName.equalsIgnoreCase(itemName) || !prices.containsKey(materialName)) return;

                int itemAmount = item.getAmount();

                totalValue.addAndGet(prices.get(materialName) * itemAmount);
                itemsSold.addAndGet(itemAmount);
                player.getInventory().remove(item);
            });

            player.sendMessage(MessageKeys.SOLD_ONE.getMessage()
                    .replace("{amount}", String.valueOf(itemsSold.get()))
                    .replace("{material}", itemName)
                    .replace("{value}", FormatType.format(totalValue.get())));
        }

        CToken.getDatabase().addToBalance(player, totalValue.get());
    }
}
