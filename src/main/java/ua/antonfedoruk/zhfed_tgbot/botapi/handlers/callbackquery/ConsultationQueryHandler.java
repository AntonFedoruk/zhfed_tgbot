package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

@Component
public class ConsultationQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public ConsultationQueryHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        SendMessage replyMessage = replyMessageService.getReplyMessage(buttonQuery.getFrom().getId(), "consultation.registration_instructions");
//        replyMessage.setReplyMarkup(buttonService.createButton("button"));
//        replyMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("consultation.registration_button")));
        replyMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("consultation.registration_button")));
        userDataCache.setUsersCurrentBotState(buttonQuery.getFrom().getId(), BotState.ASK_CONSULTATION_MESSENGER);

        System.out.println("from consultation query handler");

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.ABOUT_CONSULTATION;
    }
}
