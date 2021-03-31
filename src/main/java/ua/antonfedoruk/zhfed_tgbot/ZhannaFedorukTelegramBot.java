package ua.antonfedoruk.zhfed_tgbot;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

// This class describes our variant of TelegramWebhookBot bean, that we will declare in config files.
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZhannaFedorukTelegramBot extends TelegramWebhookBot {
    String webHookPath;
    String botUserName;
    String botToken;

    public ZhannaFedorukTelegramBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return new SendMessage(Long.toString(update.getMessage().getChatId()), "Initial: "+update.getMessage().getText());
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }
}
