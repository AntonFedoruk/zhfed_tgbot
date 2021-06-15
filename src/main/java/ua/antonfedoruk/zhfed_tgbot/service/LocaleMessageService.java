package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

// This class works with file template 'response messages' from messages_*_*.properties.
@Service
public class LocaleMessageService {
    private final Locale locale;
    private final MessageSource messageSource;
    private UsersProfileDataService usersProfileDataService;


    //@Value("${localeTag}") : inject 'localeTag' value from property file.
//    public LocaleMessageService(@Value("${localeTag}") String localeTag, MessageSource messageSource, UsersProfileDataService usersProfileDataService) {
    public LocaleMessageService(LocaleChangeInterceptor localeChangeInterceptor, MessageSource messageSource, UsersProfileDataService usersProfileDataService) {
//        this.locale = Locale.forLanguageTag(localeTag);

        String lang = localeChangeInterceptor.getParamName();
        System.out.println("*****");
        System.out.println("lang pam:  " + lang);
        System.out.println("*****");

        this.locale = Locale.forLanguageTag(lang);
        this.messageSource = messageSource;
        this.usersProfileDataService = usersProfileDataService;
    }

    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Object...args) {
        return messageSource.getMessage(message, args, locale);
    }
}
