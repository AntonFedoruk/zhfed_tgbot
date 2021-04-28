package ua.antonfedoruk.zhfed_tgbot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData implements Serializable {
    @Id
    @Column(name = "chat_id")
    Long chatId;
    String name;
    @Column(name = "phone_number")
    String phoneNumber;
    String country;
    String messenger;
}
