package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

@Component
public class YesQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService messageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public YesQueryHandler(ReplyMessageService messageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.messageService = messageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        SendMessage replyMessage = messageService.getReplyMessage(buttonQuery.getFrom().getId(),"greeting.introduction_video", Emoji.POINT_DOWN);
        // The Bot API supports basic formatting for messages. For using HTML-style, pass HTML in the parse_mode field.
        // replyMessage.setParseMode(ParseMode.HTML);
        replyMessage.setReplyMarkup(buttonService.createButtonWithUrl(messageService.getReplyText("video.watch"), "https://www.youtube.com/watch?v=--6ydjM09Zk"));

        userDataCache.setUsersCurrentBotState(buttonQuery.getFrom().getId(), BotState.CONTINUE_AFTER_INTRODUCTION_VIDEO);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.CONTINUE_BEFORE_INTRODUCTION_VIDEO;
    }
}
