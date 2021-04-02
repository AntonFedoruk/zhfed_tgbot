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

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
