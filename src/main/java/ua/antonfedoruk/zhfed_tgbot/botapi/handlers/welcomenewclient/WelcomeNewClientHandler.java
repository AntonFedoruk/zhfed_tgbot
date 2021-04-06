package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.welcomenewclient;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

/**
 * Intro 'about bot'
 * This handler is common for several bot state (look at BotStateContext.class#isWelcomeNewClientState(BotState))
 */

@Component
public class WelcomeNewClientHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService messageService;
    private CreateButtonService buttonService;

    public WelcomeNewClientHandler(UserDataCache userDataCache, ReplyMessageService messageService, CreateButtonService buttonService) {
        this.userDataCache = userDataCache;
        this.messageService = messageService;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        return welcomeNewClients(inputMessage);
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.WELCOME_NEW_CLIENT;
    }

    private SendMessage welcomeNewClients(Message inputMessage) {
        Long userId = inputMessage.getFrom().getId();
        Long chatId = inputMessage.getChatId();
        BotState botState = BotState.CONTINUE_BEFORE_ABOUT_SUCCESS;

        SendMessage replyToUser = messageService.getReplyMessage(chatId, "greeting.about_bot");
        //Add button "Continue".
        String buttonText = messageService.getReplyText("continue");
        InlineKeyboardMarkup keyboardMarkup = buttonService.createButton(buttonText);
        replyToUser.setReplyMarkup(keyboardMarkup);

        userDataCache.setUsersCurrentBotState(userId, botState);

        return replyToUser;
    }
}
