package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.NewsDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.service.ScraperService;

@Component
public class ShowMoreNewsQueryHandler implements CallbackQueryHandler {
    private NewsDataCache newsDataCache;
    private ReplyMessageService replyMessageService;
    private ZhannaFedorukTelegramBot telegramBot;
    private CreateButtonService buttonService;

    public ShowMoreNewsQueryHandler(@Lazy ZhannaFedorukTelegramBot telegramBot, NewsDataCache newsDataCache, ReplyMessageService replyMessageService, CreateButtonService buttonService) {
        this.newsDataCache = newsDataCache;
        this.telegramBot = telegramBot;
        this.replyMessageService = replyMessageService;
        this.buttonService = buttonService;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery buttonQuery) {
        Long chatId = buttonQuery.getMessage().getChatId();
        Integer messageId = buttonQuery.getMessage().getMessageId();

        //заглушка
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(String.valueOf(chatId));
        replyMessage.setText("");

        ScraperService.News newsData;
        try {
            newsData = newsDataCache.getNewsPostForUserWithChatId(String.valueOf(chatId));
        } catch (NullPointerException e) {
            telegramBot.sendPhoto(chatId, "Это все новости на сегодня!", "static/images/no_news.png");

            return replyMessage;
        }

        String headlineWithLinkInside = "<a href=\"" + newsData.getLink() + "\">" + newsData.getHeadline() + "</a>";
        String text = newsData.getText();

        EditMessageMedia editMessage = new EditMessageMedia();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        InputMediaPhoto mediaPhoto = new InputMediaPhoto();
        mediaPhoto.setMedia(telegramBot.getFileFromLink(newsData.getImageLink()), telegramBot.getFileFromLink(newsData.getImageLink()).getName());
        mediaPhoto.setCaption(headlineWithLinkInside + "\n" + text);
        mediaPhoto.setParseMode(ParseMode.HTML);
        editMessage.setMedia(mediaPhoto);
        editMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("news.button_show_more")));

        telegramBot.updateMessageMedia(editMessage);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.SHOW_MORE_NEWS_BUTTON;
    }
}