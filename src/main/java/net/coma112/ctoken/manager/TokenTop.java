package net.coma112.ctoken.manager;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.stream.IntStream;

import net.md_5.bungee.api.chat.TextComponent;

public record TokenTop(@NotNull String playerName, int balance) {
    @SuppressWarnings("deprecation")
    public static TextComponent getTopDatabase(int value) {
        List<TokenTop> topBalance = CToken.getDatabase().getTop(value);
        TextComponent message = new TextComponent(MessageProcessor.process("\n&eTop " + value + " Balances:&f\n\n"));

        IntStream.range(0, topBalance.size()).forEach(index -> {
            message.addExtra(MessageProcessor.process(
                    String.format("&f%d. &e%s &f- &7(&e%s&7)",
                            index + 1,
                            topBalance.get(index).playerName(),
                            FormatType.format(topBalance.get(index).balance()))
            ));

            if (index < topBalance.size() - 1) message.addExtra("\n");
        });

        return message;
    }
}
