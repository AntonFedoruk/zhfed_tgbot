package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

@Component
public class LanguageMenuHandler implements InputMessageHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public LanguageMenuHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(Message message) {
        Long chatId = message.getFrom().getId();
        SendMessage replyMessage = replyMessageService.getReplyMessage(chatId, "settings.choose_language");
        replyMessage.setReplyMarkup(buttonService.createButtons3psInRow(
                replyMessageService.getReplyText("settings.language_ua", Emoji.FLAG_UA),
                replyMessageService.getReplyText("settings.language_ru", Emoji.FLAG_RU),
                replyMessageService.getReplyText("settings.language_en", Emoji.FLAG_EN)
        ));
        userDataCache.setUsersCurrentBotState(chatId, BotState.CHANGE_LANGUAGE);
        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.SHOW_LANGUAGE_MENU;
    }
}