package ua.antonfedoruk.zhfed_tgbot.botapi.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;

public interface InputMessageHandler {
    SendMessage handle(Message message);

    BotState getHandlersBotState();
}
