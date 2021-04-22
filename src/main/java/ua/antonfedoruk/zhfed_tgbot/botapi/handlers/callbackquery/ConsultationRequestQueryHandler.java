package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

@Component
public class ConsultationRequestQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public ConsultationRequestQueryHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        Long chatId = buttonQuery.getFrom().getId();
        SendMessage replyMessage = replyMessageService.getReplyMessage(chatId, "consultation.registration_messenger_data");
        replyMessage.setReplyMarkup(buttonService.createButtons4ps2x2("Telegram", "Viber", "WhatsApp", "Facebook"));
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_CONSULTATION_MESSENGER);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.CONSULTATION_REQUEST;
    }
}
