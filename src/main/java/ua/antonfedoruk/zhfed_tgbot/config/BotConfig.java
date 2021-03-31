package ua.antonfedoruk.zhfed_tgbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotConfig {
    String webHookPath;
    String botUserName;
    String botToken;

    //we should set proxy, due to the fact of blocking telegram's resources on our territory
    DefaultBotOptions.ProxyType proxyType;
    String proxyHost;
    int proxyPort;

    @Bean
    public ZhannaFedorukTelegramBot zhannaFedorukTelegramBot() {
        //in this options we can set initial settings like set webHook, proxy and other
        DefaultBotOptions options = new DefaultBotOptions();

        //Proxy settings.
        options.setProxyHost(proxyHost);
        options.setProxyType(proxyType);
        options.setProxyPort(proxyPort);

        ZhannaFedorukTelegramBot zhannaFedorukTelegramBot = new ZhannaFedorukTelegramBot(options);
        zhannaFedorukTelegramBot.setBotToken(botToken);
        zhannaFedorukTelegramBot.setBotUserName(botUserName);
        zhannaFedorukTelegramBot.setWebHookPath(webHookPath);

        return zhannaFedorukTelegramBot;
    }
}