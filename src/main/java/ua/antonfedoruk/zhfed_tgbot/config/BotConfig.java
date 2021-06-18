package ua.antonfedoruk.zhfed_tgbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;
import ua.antonfedoruk.zhfed_tgbot.botapi.TelegramFacade;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotConfig {
    String webHookPath;
    String botUserName;
    String botToken;

    DefaultBotOptions.ProxyType proxyType;
    String proxyHost;
    int proxyPort;

    //   Defining the ZhannaFedorukTelegramBot Bean
    @Bean
    public ZhannaFedorukTelegramBot zhannaFedorukTelegramBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = new DefaultBotOptions();
        //Proxy settings.
        options.setProxyPort(proxyPort);
        options.setProxyType(proxyType);
        options.setProxyHost(proxyHost);

        ZhannaFedorukTelegramBot zhannaFedorukTelegramBot = new ZhannaFedorukTelegramBot(options, telegramFacade);
        zhannaFedorukTelegramBot.setBotToken(botToken);
        zhannaFedorukTelegramBot.setBotUserName(botUserName);
        zhannaFedorukTelegramBot.setWebHookPath(webHookPath);

        return zhannaFedorukTelegramBot;
    }


    //   Defining the MessageSource Bean
    // An application context delegates the message resolution to a bean with the exact name messageSource.
    // ReloadableResourceBundleMessageSource is the most common MessageSource implementation that resolves messages from resource bundles for different locales:
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        //Here, it's important to provide the basename as locale-specific file names will be resolved based on the name provided.
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    //unfortunately below doesn't work
    //maybe it is due to the fact how i set 'message' param in validation over UserProfileData.class fields (btw it all according to the https://www.baeldung.com/spring-custom-validation-message-source)
//   Defining LocalValidatorFactoryBean
// To use custom name messages in a properties file like we need to define a LocalValidatorFactoryBean and register the messageSource:
//    @Bean
//    public LocalValidatorFactoryBean getValidator() {
//        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
//        bean.setValidationMessageSource(messageSource());
//        return bean;
//    }
}
