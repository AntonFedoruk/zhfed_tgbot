package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.menu;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.cache.NewsDataCache;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.service.ScraperService;

@Component
public class NewsMenuHandler implements InputMessageHandler {
    private ZhannaFedorukTelegramBot telegramBot;
    private ReplyMessageService replyMessageService;
    //    private ScraperService newsService;
    private CreateButtonService buttonService;
    private NewsDataCache newsDataCache;

    public NewsMenuHandler(@Lazy ZhannaFedorukTelegramBot telegramBot, ReplyMessageService replyMessageService,
//                           ScraperService newsService,
                           CreateButtonService buttonService,
                           NewsDataCache newsDataCache) {
        this.telegramBot = telegramBot;
        this.replyMessageService = replyMessageService;
//        this.newsService = newsService;
        this.buttonService = buttonService;
        this.newsDataCache = newsDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        String chatId = String.valueOf(message.getChatId());


        SendMessage replyMessage = new SendMessage(chatId, "");

        ScraperService.News newsData;
        try {
            newsData = newsDataCache.getNewsPostForUserWithChatId(chatId);
        } catch (NullPointerException e) {
            telegramBot.sendPhoto(Long.parseLong(chatId), "Это все новости на сегодня!", "static/images/no_news.png");

            return replyMessage;
        }

//        if (newsData == null) {
//            telegramBot.sendPhoto(Long.parseLong(chatId), "Это все новости на сегодня!", "static/images/no_news.png");
//        } else {
        String headlineWithLinkInside = "<a href=\"" + newsData.getLink() + "\">" + newsData.getHeadline() + "</a>";
        String text = newsData.getText();

        telegramBot.sendPhoto(message.getFrom().getId(),
                headlineWithLinkInside + "\n" + text,
                newsData.getImageLink(),
                ParseMode.HTML,
                buttonService.createButton(replyMessageService.getReplyText("news.button_show_more")));
//        }

//        SendMessage replyMessage = new SendMessage();
//        replyMessage.setChatId(chatId);
//        replyMessage.setText("&#8205");
//        replyMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("news.button_show_more")));
//        replyMessage.setParseMode(ParseMode.HTML);
        return replyMessage;

//        SendMessage replyMessage = new SendMessage();
//        replyMessage.setChatId(String.valueOf(message.getFrom().getId()));
//        replyMessage.setParseMode(ParseMode.HTML);
//        replyMessage.enableWebPagePreview();
//        String photo = "<a href=\"" + newsData.getImageLink() + "\">&#8205;</a>"; //&#8205; -> never show in message

//        replyMessage.setText(photo);
//
////        SendMessage replyMessage = replyMessageService.getReplyMessage(message.getFrom().getId(), "consultation.registration_instructions", Emoji.WOMAN_TEACHER, Emoji.WINK);
//        replyMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("consultation.registration_button", Emoji.SEND_LETTER)));
////        userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.CONSULTATION_REQUEST);
////        return replyMessage;


//        SendMessage replyMessage = new SendMessage();
//        replyMessage.setChatId(String.valueOf(message.getFrom().getId()));
//        replyMessage.setParseMode(ParseMode.HTML);
//        replyMessage.setText("<a href='https://www.incruises.com/blog/620/RU_Norwegian_Cruise_Line_Offer_May_Update'>go to</a>");
//        replyMessage.disableWebPagePreview();
//        return replyMessage;
//        return replyMessageService.getWIPMessage(message.getChatId());
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.SHOW_NEWS_MENU;
    }
}