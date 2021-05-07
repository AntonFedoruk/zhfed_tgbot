package ua.antonfedoruk.zhfed_tgbot.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ua.antonfedoruk.zhfed_tgbot.service.LocaleMessageService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData implements Serializable {

    @Id
    @Column(name = "chat_id")
    Long chatId;

//    @Pattern(message = "Bad formed person name: ${validatedValue}",
//            regexp = "^[A-Z][a-z]*(\\s(([a-z]{1,3})|(([a-z]+\\')?[A-Z][a-z]*)))*$")
//    @Length(min = 2)
//    @NotNull
//    @Column(name = "NAME", nullable = false)
    String name;

    @Column(name = "phone_number", nullable = false)
    @NotBlank
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{9,10}$",
    message = "Incorrect number format! Please, follow +************ pattern.")
    String phoneNumber;

    @NotNull
    @Size(min=2, max=30, message = "Country/city name should be represent at least of 2 symbols!")
    String country;

    @NotNull
    String messenger;
}