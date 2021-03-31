package ua.antonfedoruk.zhfed_tgbot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;

@RestController
public class WebHookController {
    private final ZhannaFedorukTelegramBot telegramBot;

    public WebHookController(ZhannaFedorukTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    // This method listens for all the data that users can send and responds to accordingly.
    @PostMapping(value = "/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
