package ua.antonfedoruk.zhfed_tgbot;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.antonfedoruk.zhfed_tgbot.botapi.TelegramFacade;

// This class describes our variant of TelegramWebhookBot bean, that we will declare in config files.
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZhannaFedorukTelegramBot extends TelegramWebhookBot {
    String webHookPath;
    String botUserName;
    String botToken;

    TelegramFacade telegramFacade;

    public ZhannaFedorukTelegramBot(DefaultBotOptions options, TelegramFacade telegramFacade) {
        super(options);
        this.telegramFacade = telegramFacade;
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
        return telegramFacade.handleUpdate(update);
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @SneakyThrows
    public void sendSeveralAnswers(long pauseBetweenAnswersInSeconds, BotApiMethod<?>...methods) {
        for ( BotApiMethod<?> method : methods) {
            if (method.getMethod().equals("sendChatAction")) {
                Thread.sleep(pauseBetweenAnswersInSeconds*1000);
            }
            execute(method);
        }
    }
}
