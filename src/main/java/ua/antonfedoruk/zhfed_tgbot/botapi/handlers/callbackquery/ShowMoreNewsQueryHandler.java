package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
//        EditMessageMedia editMessageMedia;
        Long chatId = buttonQuery.getMessage().getChatId();

        //заглушка
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(String.valueOf(chatId));
        replyMessage.setText("");

        ScraperService.News newsData = null;
        try {
            newsData = newsDataCache.getNewsPostForUserWithChatId(String.valueOf(chatId));
        } catch (NullPointerException e) {
            telegramBot.sendPhoto(chatId, "Это все новости на сегодня!", "static/images/no_news.png");

            return replyMessage;
        }

//        if (newsData == null) {
//            telegramBot.sendPhoto(Long.parseLong(chatId), "Это все новости на сегодня!", "static/images/no_news.png");
//        } else {
        String headlineWithLinkInside = "<a href=\"" + newsData.getLink() + "\">" + newsData.getHeadline() + "</a>";
        String text = newsData.getText();

        telegramBot.sendPhoto(chatId,
                headlineWithLinkInside + "\n" + text,
                newsData.getImageLink(),
                ParseMode.HTML,
                buttonService.createButton(replyMessageService.getReplyText("news.button_show_more")));
//        }
        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.SHOW_MORE_NEWS_BUTTON;
    }
}