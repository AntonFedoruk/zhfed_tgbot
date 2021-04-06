package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

@Component
public class ContinueQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService messageService;
    private UserDataCache userDataCache;

    public ContinueQueryHandler(ReplyMessageService messageService, UserDataCache userDataCache) {
        this.messageService = messageService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        SendMessage replyMessage = new SendMessage();

        userDataCache.setUsersCurrentBotState(buttonQuery.getFrom().getId(), BotState.ABOUT_SUCCESS);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.CONTINUE_BEFORE_ABOUT_SUCCESS;
    }
}
