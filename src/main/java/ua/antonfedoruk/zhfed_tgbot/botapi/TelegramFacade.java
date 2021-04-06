package ua.antonfedoruk.zhfed_tgbot.botapi;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

// This class processes the Update and prepares a response to it.
@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {
    ZhannaFedorukTelegramBot telegramBot;
    ReplyMessageService replyMessageService;
    private final UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private ReplyMessageService messageService;

    // we have a loop in the initialization of the beans, so with @Lazy annotation we will delay the initialization of the bean ZhannaFedorukTelegramBot until now
    public TelegramFacade(@Lazy ZhannaFedorukTelegramBot telegramBot, ReplyMessageService replyMessageService,
                          UserDataCache userDataCache, BotStateContext botStateContext, ReplyMessageService messageService) {
        this.telegramBot = telegramBot;
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.messageService = messageService;
    }

    //Determines whether there are messages/pressed buttons ... in this Update.
    //BotApiMethod is a parent from which most of the 'methods that work with API telegrams' are inherited.
    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        //Text processing from the user.
        Message message = update.getMessage();
        if (message != null && message.hasText()) { //if there is a message
            log.info("New message from User:{}, chatId:{}, with text:{}", message.getFrom().getUserName(), message.getChatId(), message.getText());
            log.info("bot state: {}", userDataCache.getUsersCurrentBotState(message.getFrom().getId()));
            replyMessage = handleInputMessage(message);
        }

        //Handling  pressed buttons.
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("An inline button pressed byUser:{}, userId:{}, with data:{}",
                    update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getId(),
                    update.getCallbackQuery().getData());
            return handleCallbackQuery(callbackQuery);
        }
        return replyMessage;
    }

    private BotApiMethod<?> handleCallbackQuery(CallbackQuery buttonQuery) {
        final Long chatId = buttonQuery.getMessage().getChatId();
        final Long userId = buttonQuery.getFrom().getId();
        BotState botState;
        //відповідь на запит
//        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Please use main menu"); //відповідь за замовчуванням яка міститеме повідомлення з пропозицією скористатися меню і саме меню
        BotApiMethod<?> callBackAnswer = null;

        //From Continue choose buttons.
        //Set bot state according to chose button.
        if (buttonQuery.getData().equals(messageService.getReplyText("continue"))) {
            botState = BotState.CONTINUE_BEFORE_ABOUT_SUCCESS;
            SendMessage greeting = replyMessageService.getReplyMessage(chatId, "greeting");
            SendMessage aboutMe = replyMessageService.getReplyMessage(chatId, "greeting.about_me");
            SendChatAction typing = new SendChatAction();
            typing.setAction(ActionType.TYPING);
            typing.setChatId(chatId.toString());
            telegramBot.sendSeveralAnswers(4, greeting, typing, aboutMe, typing);
        } else {// Take the bot state from the cache.
            botState = userDataCache.getUsersCurrentBotState(userId);
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        return botStateContext.handleCallbackQuery(botState, buttonQuery);
    }

    @SneakyThrows // Hide 'try-catch' processing in runtime.
    private SendMessage handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        BotState botState = null;
        SendMessage replyMessage;

        // Set bot state according to entered message.
        switch (inputMessage) {
            case "/start":
                botState = BotState.WELCOME_NEW_CLIENT;
                SendMessage greeting = replyMessageService.getReplyMessage(chatId, "greeting");
                SendMessage aboutMe = replyMessageService.getReplyMessage(chatId, "greeting.about_me");
                SendMessage aboutBot = replyMessageService.getReplyMessage(chatId, "greeting.about_bot");
                SendChatAction typing = new SendChatAction();
                typing.setAction(ActionType.TYPING);
                typing.setChatId(chatId.toString());
                telegramBot.sendSeveralAnswers(5, greeting, typing, aboutMe, typing, aboutBot);
                break;
            case "/consultation_appointment":
                break;
            case "/help":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                // Take the bot state from the cache.
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }
}
