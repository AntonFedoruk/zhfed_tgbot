package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.model.Language;
import ua.antonfedoruk.zhfed_tgbot.model.UserProfileData;
import ua.antonfedoruk.zhfed_tgbot.service.MainMenuService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;
import ua.antonfedoruk.zhfed_tgbot.service.UsersProfileDataService;
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

@Component
@Slf4j
public class ChangeLanguageRequestQueryHandler implements CallbackQueryHandler {
    private ReplyMessageService replyMessageService;
    private UserDataCache userDataCache;
    private UsersProfileDataService usersProfileDataService;
    private MainMenuService mainMenuService;

    public ChangeLanguageRequestQueryHandler(ReplyMessageService replyMessageService, UserDataCache userDataCache, UsersProfileDataService usersProfileDataService, MainMenuService mainMenuService) {
        this.replyMessageService = replyMessageService;
        this.userDataCache = userDataCache;
        this.usersProfileDataService = usersProfileDataService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery) {
        Long chatId = buttonQuery.getFrom().getId();

        String buttonQueryText = buttonQuery.getData();
        String textFromTheButtonWithSelectedLanguage = buttonQueryText.substring(buttonQueryText.indexOf("\"") + 1, buttonQueryText.lastIndexOf("\""));
        log.info("User with chatId: " + chatId + " choose " + textFromTheButtonWithSelectedLanguage + "button.");

        Language language = usersProfileDataService.getUserLanguage(chatId);

        if (textFromTheButtonWithSelectedLanguage.contains(replyMessageService.getReplyText("settings.language_ua", Emoji.FLAG_UA))) {
            language = Language.UKRAINE;
        } else if (textFromTheButtonWithSelectedLanguage.contains(replyMessageService.getReplyText("settings.language_en", Emoji.FLAG_EN))) {
            language = Language.ENGLISH;
        } else if (textFromTheButtonWithSelectedLanguage.contains(replyMessageService.getReplyText("settings.language_ru", Emoji.FLAG_RU))) {
            language = Language.RUSSIA;
        } else {
            log.info("No match with 'enumed' Language.");

            return replyMessageService.getReplyMessage(chatId, "setting.new_language_error");
        }

//        usersProfileDataService.setUserLanguage(chatId, language);

        // save to DB
//        //UserProfileData userProfileData = usersProfileDataService.getUserProfileData(chatId);
//        //userProfileData.setLanguage(language);
//        //usersProfileDataService.saveUsersProfileData(userProfileData);
        usersProfileDataService.setUserLanguage(chatId, language);

//        userDataCache.setUsersCurrentBotState(chatId, BotState.SHOW_MAIN_MENU);

//        return replyMessageService.getReplyMessage(chatId, "setting.new_language");
        return mainMenuService.getMainMenuMessage(String.valueOf(chatId),
                replyMessageService.getReplyText("setting.new_language"));
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.CHANGE_LANGUAGE;
    }
}