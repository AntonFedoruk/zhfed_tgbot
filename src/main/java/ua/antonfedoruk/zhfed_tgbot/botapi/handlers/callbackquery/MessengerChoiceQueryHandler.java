package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.model.UserProfileData;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

@Component
public class MessengerChoiceQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;

    public MessengerChoiceQueryHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        Long userId = buttonQuery.getFrom().getId();
        Long chatId = buttonQuery.getMessage().getChatId();
        SendMessage replyMessage = replyMessageService.getReplyMessage(chatId, "consultation.registration_phone_data");

        String callbackString = buttonQuery.getData();
        String messenger = callbackString.substring(callbackString.indexOf("\"") + 1, callbackString.lastIndexOf("\""));
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        userProfileData.setMessenger(messenger);
        userDataCache.saveUserProfileData(userId, userProfileData);
        //regex
        /*
            String pattern = "\\\".+\\\"";
            // Создание Pattern объекта
            Pattern r = Pattern.compile(pattern);
            // Создание matcher объекта
            Matcher m = r.matcher(callbackString);
            if (m.find()) {
                messenger = callbackString.substring(m.start(), m.end());
            }
        */

        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CONSULTATION_PHONE);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.ASK_CONSULTATION_MESSENGER;
    }
}
