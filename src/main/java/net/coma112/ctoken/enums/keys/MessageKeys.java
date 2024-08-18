package net.coma112.ctoken.enums.keys;

import net.coma112.ctoken.CToken;
import net.coma112.ctoken.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum MessageKeys {
    RELOAD("message.reload"),
    INVALID_VALUE("message.invalid-value"),
    TARGET_DONT_EXIST("message.target-dont-exists"),
    ADD_SENDER("message.add-sender"),
    ADD_TARGET("message.add-target"),
    ADD_EVERYONE_SENDER("message.add-everyone-sender"),
    NOT_ENOUGH_TOKEN("message.not-enough-token"),
    PAY_SENDER("message.pay-sender"),
    PAY_TARGET("message.pay-target"),
    RESET_EVERYONE_SENDER("message.reset-everyone-sender"),
    RESET_SENDER("message.reset-sender"),
    RESET_TARGET("message.reset-target"),
    SET_SENDER("message.set-sender"),
    SET_TARGET("message.set-target"),
    TAKE_SENDER("message.take-sender"),
    TAKE_TARGET("message.take-target"),
    BALANCE("message.balance"),
    INVALID_ITEM("message.invalid-item"),
    SOLD_ONE("message.sold-one"),
    SOLD_ALL("message.sold-all"),
    WORTH_HEADER("message.worth.header"),
    NO_VALUE("message.no-value"),
    PLAYER_REQUIRED("message.player-required"),
    NO_PERMISSION("message.no-permission"),
    MISSING_ARGUMENT("message.missing-argument"),
    INVALID_NUMBER("message.invalid-number"),
    PAYMENT_DISABLED("message.payment-is-disabled-try"),
    ENTER_NUMBER("message.enter-number"),
    NOT_ENOUGH_PAYMENT("message.not-enough-minimum-payment"),
    DISABLE_PAY("message.disable-pay"),
    ENABLE_PAY("message.enable-pay"),
    WORTH_ITEM("message.worth.item"),
    WORTH_TOTAL("message.worth.total"),
    TOKEN_TOP_HEADER("message.token-top.header"),
    TOKEN_TOP_LINE("message.token-top.line"),
    HELP("message.help");

    private final String path;

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public String getMessage() {
        return MessageProcessor.process(CToken.getInstance().getLanguage().getString(path));
    }

    public List<String> getMessages() {
        return CToken.getInstance().getLanguage().getList(path)
                .stream()
                .map(MessageProcessor::process)
                .toList();
    }
}

