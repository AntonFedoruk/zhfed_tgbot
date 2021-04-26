package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

@Component
public class AboutConsultationQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public AboutConsultationQueryHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        SendMessage replyMessage = replyMessageService.getReplyMessage(buttonQuery.getFrom().getId(), "consultation.registration_instructions", Emoji.WOMAN_TEACHER, Emoji.WINK);
        replyMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("consultation.registration_button", Emoji.SEND_LETTER)));
        userDataCache.setUsersCurrentBotState(buttonQuery.getFrom().getId(), BotState.CONSULTATION_REQUEST);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.ABOUT_CONSULTATION;
    }
}
