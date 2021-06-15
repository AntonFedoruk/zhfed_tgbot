package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ua.antonfedoruk.zhfed_tgbot.model.Language;
import ua.antonfedoruk.zhfed_tgbot.model.UserProfileData;
import ua.antonfedoruk.zhfed_tgbot.repository.UsersProfileDataRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@Service
@Validated
public class UsersProfileDataService {
    private UsersProfileDataRepository usersProfileDataRepository;

    public UsersProfileDataService(UsersProfileDataRepository usersProfileDataRepository) {
        this.usersProfileDataRepository = usersProfileDataRepository;
    }

    public List<UserProfileData> getAllProfiles() {
        return (List<UserProfileData>) usersProfileDataRepository.findAll();
    }

    //In case of validation fail ConstraintViolationException and 500 error will be thrown.
    public void saveUsersProfileData(@Valid UserProfileData userProfileData) {
        usersProfileDataRepository.save(userProfileData);
    }

    public Language getUserLanguage(Long chatId) {
        return usersProfileDataRepository.findByChatId(chatId).getLanguage();
    }

    public void deleteUsersProfileData(String profileDataId) {
        usersProfileDataRepository.deleteById(profileDataId);
    }

    public UserProfileData getUserProfileData(Long chatId) {
        return usersProfileDataRepository.findByChatId(chatId);
    }
}