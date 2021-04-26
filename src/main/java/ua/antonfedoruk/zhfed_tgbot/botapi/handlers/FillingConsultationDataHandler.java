package ua.antonfedoruk.zhfed_tgbot.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.model.UserProfileData;
import ua.antonfedoruk.zhfed_tgbot.service.CreateButtonService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

@Component
public class FillingConsultationDataHandler implements InputMessageHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;
    private CreateButtonService buttonService;

    public FillingConsultationDataHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache, CreateButtonService buttonService) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.buttonService = buttonService;
    }

    @Override
    public SendMessage handle(Message message) {
        String usersAnswer = message.getText();
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyMessage = null;

        if (botState.equals(BotState.ABOUT_CONSULTATION)) {
            replyMessage = replyMessageService.getReplyMessage(chatId, "consultation.registration_instructions", Emoji.WOMAN_TEACHER, Emoji.SLIGHTLY_SMILING_FACE);
            replyMessage.setReplyMarkup(buttonService.createButton(replyMessageService.getReplyText("consultation.registration_button", Emoji.SEND_LETTER)));
            userDataCache.setUsersCurrentBotState(userId, BotState.CONSULTATION_REQUEST);
        }

        if (botState.equals(BotState.ASK_CONSULTATION_PHONE)) {
            profileData.setPhoneNumber(usersAnswer);
            replyMessage = replyMessageService.getReplyMessage(chatId, "consultation.registration_country_data");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CONSULTATION_COUNTRY);
        }

        if (botState.equals(BotState.ASK_CONSULTATION_COUNTRY)) {
            profileData.setCountry(usersAnswer);

            //final answer
            String successRegistrationMessage = replyMessageService.getReplyText("consultation.registration_success", Emoji.INBOX_TRAY);
            replyMessage = new SendMessage(chatId.toString(), String.format("%s%n(%s, %s, %s)",successRegistrationMessage, profileData.getMessenger(),
                    profileData.getPhoneNumber(), profileData.getCountry()));

            userDataCache.setUsersCurrentBotState(userId, BotState.CONSULTATION_DATA_FILLED);
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyMessage;
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.FILLING_CONSULTATION_DATA;
    }
}