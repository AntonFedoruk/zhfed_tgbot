package ua.antonfedoruk.zhfed_tgbot.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.antonfedoruk.zhfed_tgbot.model.Language;
import ua.antonfedoruk.zhfed_tgbot.model.UserProfileData;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface UsersProfileDataRepository extends CrudRepository<UserProfileData, String> {
    UserProfileData findByChatId(Long chatId);

    void deleteByChatId(Long chatId);
}