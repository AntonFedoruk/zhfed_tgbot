package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

// This class works with file template 'response messages' from messages_*_*.properties.
@Service
public class LocaleMessageService {
    private final Locale locale;
    private final MessageSource messageSource;


    //@Value("${localeTag}") : inject 'localeTag' value from property file.
    public LocaleMessageService(@Value("${localeTag}") String localeTag, MessageSource messageSource) {
        this.locale = Locale.forLanguageTag(localeTag);
        this.messageSource = messageSource;
    }

    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Object...args) {
        return messageSource.getMessage(message, args, locale);
    }
}
