package ua.antonfedoruk.zhfed_tgbot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery.CallbackQueryHandler;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Defines message handlers for each bot state.
@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();
    private Map<BotState, CallbackQueryHandler> callbackQueryHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers, List<CallbackQueryHandler> callbackQueryHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlersBotState(), handler));
        callbackQueryHandlers.forEach(handler -> this.callbackQueryHandlers.put(handler.getHandlersBotState(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    public BotApiMethod<?> handleCallbackQuery(BotState currentState, CallbackQuery buttonQuery) {
        CallbackQueryHandler callbackQueryHandler = findCallbackQueryHandler(currentState);
        return callbackQueryHandler.handle(buttonQuery);
    }

    private CallbackQueryHandler findCallbackQueryHandler(BotState currentState) {
        if (isContinueButtonDisplayed(currentState)) {
            return callbackQueryHandlers.get(BotState.CONTINUE_BUTTONS);
        }
        return callbackQueryHandlers.get(currentState);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isAskForConsultation(currentState)) {
            return messageHandlers.get(BotState.FILLING_CONSULTATION_DATA);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isAskForConsultation(BotState currentState) {
        switch (currentState) {
            case ASK_CONSULTATION_COUNTRY:
            case ASK_CONSULTATION_PHONE:
            case ASK_CONSULTATION_MESSENGER:
                return true;
            default:
                return false;
        }
    }

    private boolean isContinueButtonDisplayed(BotState currentState) {
        switch (currentState) {
            case CONTINUE_BUTTONS:
            case CONTINUE_BEFORE_ABOUT_SUCCESS:
            case CONTINUE_AFTER_INTRODUCTION_VIDEO:
            case VIDEOS_CONCLUSION:
                return true;
            default:
                return false;
        }
    }
}
