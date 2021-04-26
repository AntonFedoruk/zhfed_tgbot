package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

@Component
public class NewsMenuHandler implements InputMessageHandler {
    private ReplyMessageService replyMessageService;

    public NewsMenuHandler(ReplyMessageService replyMessageService) {
        this.replyMessageService = replyMessageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return replyMessageService.getWIPMessage(message.getChatId());
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.SHOW_NEWS_MENU;
    }
}