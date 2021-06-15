package ua.antonfedoruk.zhfed_tgbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import org.springframework.security.core.context.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Configuration
@Slf4j
public class DBLocaleResolver extends CookieLocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication().getPrincipal() instanceof OidcUser) {
            OidcUser user = (OidcUser) securityContext.getAuthentication().getPrincipal();
            logger.info("Setting locale from OidcUser: {}", user.getLocale());
            return Locale.forLanguageTag(user.getLocale());
        } else {
            return request.getLocale();
        }
    }
}
