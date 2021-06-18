package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class works with file template 'response messages' from messages_*_*.properties.
@Service
@RestControllerAdvice
public class LocaleMessageService implements HandlerInterceptor, RequestBodyAdvice {
    private Locale locale;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UsersProfileDataService usersProfileDataService;

    //@Value("${localeTag}") : inject 'localeTag' value from property file.
//    public LocaleMessageService(@Value("${localeTag}") String localeTag, MessageSource messageSource, UsersProfileDataService usersProfileDataService) {
    public LocaleMessageService() {
//
//        this.locale = Locale.forLanguageTag(usersProfileDataService.getUserLanguage());
//        this.messageSource = messageSource;
//        this.usersProfileDataService = usersProfileDataService;
    }

    public String getMessage(String message) {
        Locale locale = Locale.forLanguageTag(usersProfileDataService.getUserLanguage(Long.valueOf(getChatIdFromRequest())).getLanguageTag());
        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Object... args) {
        Locale locale = Locale.forLanguageTag(usersProfileDataService.getUserLanguage(Long.valueOf(getChatIdFromRequest())).getLanguageTag());
        return messageSource.getMessage(message, args, locale);
    }

    private String getChatIdFromRequest() {
        String payloadRequest = requestInfoThreadLocal.get().body;

        //get chat_id
        Pattern pattern = Pattern.compile("(?<=Chat\\(id=)\\d+?(?=,)");
        Matcher matcher = pattern.matcher(payloadRequest);
        String chatId = null;
        if (matcher.find()) {
            chatId = payloadRequest.substring(matcher.start(), matcher.end());
        }

        return chatId;
    }

    //all bellow is for obtaining chatId

    private String chatId; // throw which we can connect to user

    //Используем перехватчик(HandlerInterceptor), что бы получить другую информацию о запросе, такую как метод HTTP и параметры запроса.
    //Зафиксируем всю эту информацию запроса для сообщения об ошибках в переменной ThreadLocal, которую будем очищать на крючке afterCompletion в том же перехватчике.
//Класс java.lang.ThreadLocal<T> используется для хранения переменных, которые должны быть доступны для всего потока. Фактически это нечто вроде ещё одной области видимости переменных. Класс ThreadLocal  имеет методы get  и set, которые позволяют получить текущее значение и установить новое значение.
//Обычно экземпляры ThreadLocal  объявляются как приватные статические переменные в классе. Каждый поток получает из метода get своё значение и устанавливает через set тоже своё значение, изолированное от других потоков.
    private static final ThreadLocal<LocaleMessageService> requestInfoThreadLocal = new ThreadLocal<>();

    private String method;
    private String body;
    private String queryString;
    private String ip;
    private String user;
    private String referrer;
    private String url;

    public static LocaleMessageService get() {
        LocaleMessageService requestInfo = requestInfoThreadLocal.get();
        if (requestInfo == null) {
            requestInfo = new LocaleMessageService();
            requestInfoThreadLocal.set(requestInfo);
        }
        return requestInfo;
    }

    public Map<String, String> asMap() {
        Map<String, String> map = new HashMap<>();
        map.put("method", this.method);
        map.put("url", this.url);
        map.put("queryParams", this.queryString);
        map.put("body", this.body);
        map.put("ip", this.ip);
        map.put("referrer", this.referrer);
        map.put("user", this.user);
        return map;
    }

    private void setInfoFromRequest(HttpServletRequest request) {
        this.method = request.getMethod();
        this.queryString = request.getQueryString();
        this.ip = request.getRemoteAddr();
        this.referrer = request.getRemoteHost();
        this.url = request.getRequestURI();
        if (request.getUserPrincipal() != null) {
            this.user = request.getUserPrincipal().getName();
        }
    }

    public void setBody(String body) {
        this.body = body;
    }

    private static void setInfoFrom(HttpServletRequest request) {
        LocaleMessageService requestInfo = requestInfoThreadLocal.get();
        if (requestInfo == null) {
            requestInfo = new LocaleMessageService();
        }
        requestInfo.setInfoFromRequest(request);
        requestInfoThreadLocal.set(requestInfo);
    }

    private static void clear() {
        requestInfoThreadLocal.remove();
    }

    private static void setBodyInThreadLocal(String body) {
        LocaleMessageService requestInfo = get();
        requestInfo.setBody(body);
        setRequestInfo(requestInfo);
    }

    private static void setRequestInfo(LocaleMessageService requestInfo) {
        requestInfoThreadLocal.set(requestInfo);
    }

    // Implementation of HandlerInterceptor to capture the request info (except body) and be able to add it to the report in case of an error

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LocaleMessageService.setInfoFrom(request);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LocaleMessageService.clear();
    }

    // Implementation of RequestBodyAdvice to capture the request body and be able to add it to the report in case of an error

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        LocaleMessageService.setBodyInThreadLocal(body.toString());
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
