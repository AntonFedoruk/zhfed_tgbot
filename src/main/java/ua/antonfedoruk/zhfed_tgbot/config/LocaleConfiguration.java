package ua.antonfedoruk.zhfed_tgbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

//  Defining locale
//tutorial: https://www.baeldung.com/spring-boot-internationalization#localechangeinterceptor
@Configuration
public class LocaleConfiguration implements WebMvcConfigurer {
//    private final Locale DEFAULT_LOCALE = Locale.forLanguageTag("ru-RU");

    // In order for our application to be able to determine which locale is currently being used, we need to add a LocaleResolver bean:
    @Bean //localeResolver - Определяет способ получения информации Locale, которым будет пользоваться пользователь.
    public LocaleResolver localeResolver() {
//  The LocaleResolver comes from very smart interface definitions which makes use of not one but four different techniques to determine current locale, these are:
//        SessionLocaleResolver: uses a locale attribute in the user’s session
//        CookieLocaleResolver: store current locale in the browser’s cookie
//        AcceptHeaderLocaleResolver: uses the primary locale specified in the “accept-language” header of the HTTP request
//        FixedLocaleResolver: always return a fixed default locale
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
//        LocaleResolver localeResolver = new LocaleResolver() {
//            @Override
//            public Locale resolveLocale(HttpServletRequest request) {
//                request.
//                return null;
//            }
//
//            @Override
//            public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
//
//            }
//        };
//        return localeResolver;
        return slr;
    }
    //The LocaleResolver interface has implementations that determine the current locale based on the session, cookies, the Accept-Language header, or a fixed value.
    //In our example, we have used the session based resolver SessionLocaleResolver and set a default locale with value ENGLISH.


    // Next, we need to add an interceptor bean that will switch to a new locale based on the value of the lang parameter appended to a request:
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    //!! In order to take effect, this bean needs to be added to the application's interceptor registry. !!
    //To achieve this, our @Configuration class has to implement the WebMvcConfigurer interface and override the addInterceptors() method:

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
//        registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/*");
    }
}