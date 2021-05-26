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
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

// This class processes the Update and prepares a response to it.
@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {
    private ZhannaFedorukTelegramBot telegramBot;
    private ReplyMessageService replyMessageService;
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
            log.info("An inline button pressed byUser:{}, chatId:{}, with data:{}",
                    update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getData());
            log.info("bot state: {}", userDataCache.getUsersCurrentBotState(callbackQuery.getFrom().getId()));
            return handleCallbackQuery(callbackQuery);
        }
        return replyMessage;
    }

    private BotApiMethod<?> handleCallbackQuery(CallbackQuery buttonQuery) {
        final Long chatId = buttonQuery.getMessage().getChatId();
        final Long userId = buttonQuery.getFrom().getId();
        BotState botState;

        //default callback answers
        //this answer contain main menu, and propose to use it
//        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId.toString(), replyMessageService.getReplyText("main_menu.use_main_menu"));
        BotApiMethod<?> callBackAnswer;
        //Set bot state according to chose button.

        //From 'Continue' choose buttons.
        if (buttonQuery.getData().equals("Button \"" + messageService.getReplyText("continue", Emoji.ARROW_DOWN) + "\" has been pressed")
                && userDataCache.getUsersCurrentBotState(userId).equals(BotState.CONTINUE_BEFORE_ABOUT_SUCCESS)) {
            botState = BotState.CONTINUE_BEFORE_ABOUT_SUCCESS;
            System.out.println("In telegramFacade handleCallbackQuery's if(From 'Continue' choose buttons) statement ");
            telegramBot.sendPhoto(chatId, null, "static/images/contracts-and-agreements.jpg");

            SendMessage aboutSuccess = replyMessageService.getReplyMessage(chatId, "greeting.about_success", Emoji.PEN);
            SendMessage mySuccess = replyMessageService.getReplyMessage(chatId, "greeting.my_success", Emoji.WINK);

            SendChatAction typing = new SendChatAction();
            typing.setAction(ActionType.TYPING);
            typing.setChatId(chatId.toString());

            telegramBot.sendSeveralAnswers(4, aboutSuccess, typing, mySuccess, typing);
        }

        //From 'Yes' choose buttons.
        else if (buttonQuery.getData().equals("Button \"" + messageService.getReplyText("continue_yes") + "\" has been pressed")) {
            botState = BotState.CONTINUE_BEFORE_INTRODUCTION_VIDEO;

            BotApiMethod<?> sendButtonWithVideo = botStateContext.handleCallbackQuery(botState, buttonQuery);

            SendChatAction typing = new SendChatAction();
            typing.setAction(ActionType.TYPING);
            typing.setChatId(chatId.toString());

            telegramBot.sendSeveralAnswers(5, sendButtonWithVideo, typing);

            //at this position user may press 'button to watch video'

            botState = BotState.CONTINUE_AFTER_INTRODUCTION_VIDEO;
        }

        //From 'Continue' button after video-button.
        else if (buttonQuery.getData().equals("Button \"" + messageService.getReplyText("continue", Emoji.ARROW_DOWN) + "\" has been pressed")
                && userDataCache.getUsersCurrentBotState(userId).equals(BotState.VIDEOS_CONCLUSION)) {
            BotApiMethod<?> needsForSuccess = messageService.getReplyMessage(chatId, "greeting.steps_for_success", Emoji.NOTES, Emoji.DOT);

            BotApiMethod<?> necessaryQualities = messageService.getReplyMessage(chatId, "greeting.dont_worry", Emoji.BLUSH);

            SendChatAction typing = new SendChatAction();
            typing.setAction(ActionType.TYPING);
            typing.setChatId(chatId.toString());

            telegramBot.sendSeveralAnswers(3, typing, needsForSuccess);

            telegramBot.sendSeveralAnswers(5, typing, necessaryQualities, typing);

            botState = BotState.VIDEOS_CONCLUSION;
        }

// something wrong with this callback(can't see callback from button with link inside)
//
//        //From 'Watch' choose buttons.
//        else if (buttonQuery.getData().equals("Button \"" + messageService.getReplyText("video.watch") + "\" has been pressed")) {
//            botState = BotState.CONTINUE_AFTER_INTRODUCTION_VIDEO;
//        }

        //From 'Get' consultation button.
        else if (buttonQuery.getData().equals("Button \"" + messageService.getReplyText("consultation.get_button_text", Emoji.GIFT) + "\" has been pressed")) {
            BotApiMethod<?> intro = messageService.getReplyMessage(chatId, "consultation.intro");
            BotApiMethod<?> youWillGet = messageService.getReplyMessage(chatId, "consultation.you_will_get", Emoji.BOOK);
            BotApiMethod<?> youWillGet1 = messageService.getReplyMessage(chatId, "consultation.you_will_get_1");
            BotApiMethod<?> youWillGet2 = messageService.getReplyMessage(chatId, "consultation.you_will_get_2");
            BotApiMethod<?> youWillGet3 = messageService.getReplyMessage(chatId, "consultation.you_will_get_3");
            BotApiMethod<?> youWillGet4 = messageService.getReplyMessage(chatId, "consultation.you_will_get_4");

            SendChatAction typing = new SendChatAction();
            typing.setAction(ActionType.TYPING);
            typing.setChatId(chatId.toString());

            telegramBot.sendSeveralAnswers(2, typing, intro, typing, youWillGet, typing, youWillGet1,
                    typing, youWillGet2, typing, youWillGet3, typing, youWillGet4, typing);

            botState = BotState.ABOUT_CONSULTATION;
        } else {// Take the bot state from the cache.
            log.info("telegramFacade > handleCallbackQuery() > else block (Unpredictable statement! -> Take the bot state from the cache!)");
            botState = userDataCache.getUsersCurrentBotState(userId);
        }

        log.info("go to handler with key: {}", botState);

        userDataCache.setUsersCurrentBotState(userId, botState);

//        if (botStateContext.handleCallbackQuery(botState, buttonQuery) != null)
        callBackAnswer = botStateContext.handleCallbackQuery(botState, buttonQuery);

        return callBackAnswer;
    }

    @SneakyThrows // Hide 'try-catch' processing in runtime.
    private SendMessage handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        // Set bot state according to entered message.
        if ("/start".equals(inputMessage) || "Знакомство с ботом".equals(inputMessage)) {
            botState = BotState.WELCOME_NEW_CLIENT;
            SendMessage greeting = replyMessageService.getReplyMessage(chatId, "greeting");
            SendMessage aboutMe = replyMessageService.getReplyMessage(chatId, "greeting.about_me", Emoji.MONEY);
            SendChatAction typing = new SendChatAction();
            typing.setAction(ActionType.TYPING);
            typing.setChatId(chatId.toString());
            telegramBot.sendSeveralAnswers(5, greeting, typing, aboutMe, typing);
        } else if ("/consultation_appointment".equals(inputMessage) || "Записаться на консультацию".equals(inputMessage)) {
            botState = BotState.ABOUT_CONSULTATION;
        } else if ("/help".equals(inputMessage) || "Помощь".equals(inputMessage)) {
            botState = BotState.SHOW_HELP_MENU;
        } else if ("Новости".equals(inputMessage)) {
            botState = BotState.SHOW_NEWS_MENU;
        } else if ("/unsibscribe".equals(inputMessage) || "/exit".equals(inputMessage) || "Отписаться от рассылки".equals(inputMessage)) {
            botState = BotState.SHOW_EXIT_MENU;
        } else {// Take the bot state from the cache.
            botState = userDataCache.getUsersCurrentBotState(userId);
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }
}