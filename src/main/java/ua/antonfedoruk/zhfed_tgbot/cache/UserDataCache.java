package ua.antonfedoruk.zhfed_tgbot.cache;

import org.springframework.stereotype.Component;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.model.UserProfileData;

import java.util.HashMap;
import java.util.Map;

//In-memory cache
@Component
public class UserDataCache implements DataCache {
    private Map<Long, BotState> usersBotState = new HashMap<>(); //<userId, botState>
    private Map<Long, UserProfileData> usersProfileData = new HashMap<>(); //<userId, userProfile>

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotState.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotState.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }
        return botState;
    }

    @Override
    public UserProfileData getUserProfileData(long userId) {
        UserProfileData profileData = usersProfileData.get(userId);
        if (profileData == null) {
            profileData = new UserProfileData();
        }
        return profileData;
    }

    @Override
    public void saveUserProfileData(long userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }
}
