package net.coma112.ctoken.manager;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.MessageKeys;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public record TokenTop(@NotNull String playerName, int balance) {
    public static String getTopDatabase(int value) {
        List<TokenTop> topBalance = CToken.getDatabase().getTop(value);
        List<String> tokenTopLines = new ArrayList<>();

        tokenTopLines.add("\n \n");
        tokenTopLines.add(MessageKeys.TOKEN_TOP_HEADER.getMessage().replace("{value}", String.valueOf(value)));
        tokenTopLines.add("\n \n");

        IntStream
                .range(0, topBalance.size())
                 .forEach(index -> {
            tokenTopLines.add(MessageKeys.TOKEN_TOP_LINE.getMessage()
                    .replace("{place}", String.valueOf(index + 1))
                    .replace("{player}", topBalance.get(index).playerName())
                    .replace("{balance}", FormatType.format(topBalance.get(index).balance())));
            tokenTopLines.add("\n \n");
        });

        return String.join("\n", tokenTopLines);
    }
}
