package ua.antonfedoruk.zhfed_tgbot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData implements Serializable {
    Long chatId;
    String name;
    String phoneNumber;
    String country;
    String messenger;
}
