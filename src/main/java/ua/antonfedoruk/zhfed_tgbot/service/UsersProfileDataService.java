package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.stereotype.Service;
import ua.antonfedoruk.zhfed_tgbot.model.UserProfileData;
import ua.antonfedoruk.zhfed_tgbot.repository.UsersProfileDataRepository;

import java.util.List;

@Service
public class UsersProfileDataService {
    private UsersProfileDataRepository usersProfileDataRepository;

    public UsersProfileDataService(UsersProfileDataRepository usersProfileDataRepository) {
        this.usersProfileDataRepository = usersProfileDataRepository;
    }

    public List<UserProfileData> getAllProfiles() {
        return (List<UserProfileData>) usersProfileDataRepository.findAll();
    }

    public void saveUsersProfileData(UserProfileData userProfileData) {
        usersProfileDataRepository.save(userProfileData);
    }

    public void deleteUsersProfileData(String profileDataId) {
        usersProfileDataRepository.deleteById(profileDataId);
    }

    public UserProfileData getUserProfileData(String chatId) {
        return usersProfileDataRepository.findByChatId(chatId);
    }
}
