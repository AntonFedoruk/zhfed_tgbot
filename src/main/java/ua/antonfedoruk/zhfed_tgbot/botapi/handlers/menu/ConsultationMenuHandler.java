package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

@Component
public class ConsultationMenuHandler implements InputMessageHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public ConsultationMenuHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(Message message) {
        SendMessage replyMessage = replyMessageService.getReplyMessage(message.getFrom().getId(), "consultation.registration_instructions");
        replyMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("consultation.registration_button")));
        userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.CONSULTATION_REQUEST);
        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.ABOUT_CONSULTATION;
    }
}