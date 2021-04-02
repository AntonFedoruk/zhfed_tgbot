package ua.antonfedoruk.zhfed_tgbot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Defines message handlers for each bot state.
@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlersBotState(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        //
        if (isWelcomeNewClientState(currentState)) {
            return messageHandlers.get(BotState.WELCOME_NEW_CLIENT);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isWelcomeNewClientState(BotState currentState) {
        switch (currentState) {
            case GREETING:
            case ABOUT_ME:
            case FILLING_PROFILE:
            case PROFILE_FILLED:
                return true;
            default:
                return false;
        }
    }
}
