package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

// Handle

@Component
public class ContinueQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService messageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public ContinueQueryHandler(ReplyMessageService messageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.messageService = messageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        SendMessage replyMessage = messageService.getReplyMessage(buttonQuery.getFrom().getId(),"greeting.are_you_ready");
        replyMessage.setReplyMarkup(buttonService.createButton(messageService.getReplyText("continue_yes")));

        userDataCache.setUsersCurrentBotState(buttonQuery.getFrom().getId(), BotState.CONTINUE_BEFORE_INTRODUCTION_VIDEO);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.CONTINUE_BEFORE_ABOUT_SUCCESS;
    }
}
