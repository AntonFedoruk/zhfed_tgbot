package ua.antonfedoruk.zhfed_tgbot.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

/**
 * Intro 'about bot'
 * This handler is common for several bot state (look at BotStateContext.class#isWelcomeNewClientState(BotState))
 */

@Component
public class WelcomeNewClientHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService messageService;

    public WelcomeNewClientHandler(UserDataCache userDataCache, ReplyMessageService messageService) {
        this.userDataCache = userDataCache;
        this.messageService = messageService;
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
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;
        //v1
//        switch (botState) { typing,
//            case GREETING:
//                replyToUser = messageService.getReplyMessage(chatId, "greeting");
//                System.out.println(replyToUser.getText());
//                botState = BotState.ABOUT_ME;
//                break;
//            case ABOUT_ME:
//                replyToUser = messageService.getReplyMessage(chatId, "greeting.about_me");
//                botState = BotState.ABOUT_BOT;
//                break;
//            case ABOUT_BOT:
//                replyToUser = messageService.getReplyMessage(chatId, "greeting.about_bot");
//                botState = BotState.WELCOME_NEW_CLIENT_COMPLETED;
//                break;
//        }

        //v2
        replyToUser = new SendMessage(chatId.toString(), "Here should be button 'Next'");


        userDataCache.setUsersCurrentBotState(userId, botState);

        return replyToUser;
    }
}
