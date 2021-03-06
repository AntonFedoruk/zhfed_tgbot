package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;

public interface CallbackQueryHandler {
    BotApiMethod<?> handle(CallbackQuery buttonQuery);

    BotState getHandlersBotState();
}