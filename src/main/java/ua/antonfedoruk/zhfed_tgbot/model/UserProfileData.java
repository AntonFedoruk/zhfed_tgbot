package ua.antonfedoruk.zhfed_tgbot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
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
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{9,10}$", message = "validation.exception_phone")
//    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{9,10}$", message = "{validation.exception_phone}") it could be a part of LocalValidatorFactoryBean, but it cannot return text instead of '{validation.exception_phone}'
            String phoneNumber;

    @NotNull
    @Size(min = 2, max = 30, message = "validation.exception_country")
    String country;

    @NotNull
    String messenger;

    @Enumerated(EnumType.STRING)
    Language language = Language.ENGLISH;
}