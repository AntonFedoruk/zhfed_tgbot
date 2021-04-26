package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

// Handle

@Component
public class ContinueQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService messageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public ContinueQueryHandler(ReplyMessageService messageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.messageService = messageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        SendMessage replyMessage = null;
        BotState botState = null;
        Long chatId = buttonQuery.getMessage().getChatId();
        Long userId = buttonQuery.getFrom().getId();

        if (userDataCache.getUsersCurrentBotState(userId).equals(BotState.CONTINUE_BEFORE_ABOUT_SUCCESS)) {
            replyMessage = messageService.getReplyMessage(chatId, "greeting.are_you_ready", Emoji.PEN);
            replyMessage.setReplyMarkup(buttonService.createButton(messageService.getReplyText("continue_yes")));
            botState = BotState.CONTINUE_BEFORE_INTRODUCTION_VIDEO;
        }

        if (userDataCache.getUsersCurrentBotState(userId).equals(BotState.CONTINUE_AFTER_INTRODUCTION_VIDEO)) {
            replyMessage = messageService.getReplyMessage(chatId, "greeting.after_video", Emoji.LIKE, Emoji.CLAP);
            replyMessage.setReplyMarkup(buttonService.createButton(messageService.getReplyText("continue", Emoji.ARROW_DOWN)));
            botState = BotState.VIDEOS_CONCLUSION;
        }

        if (userDataCache.getUsersCurrentBotState(userId).equals(BotState.VIDEOS_CONCLUSION)) {
            replyMessage = messageService.getReplyMessage(chatId, "consultation.present_from_me", Emoji.GIFT, Emoji.POINT_DOWN);
            replyMessage.setReplyMarkup(buttonService.createButton(messageService.getReplyText("consultation.get_button_text", Emoji.GIFT)));
            botState = BotState.ABOUT_CONSULTATION;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.CONTINUE_BUTTONS;
    }
}
