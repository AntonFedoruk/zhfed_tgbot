package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.antonfedoruk.zhfed_tgbot.ZhannaFedorukTelegramBot;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.cache.UserDataCache;
import ua.antonfedoruk.zhfed_tgbot.utils.Emoji;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//Spring supports exception handling by a global Exception Handler (@ExceptionHandler) with Controller Advice (@RestControllerAdvice).

//The @RestControllerAdvice annotation is specialization of @Component annotation so that it is auto-detected via classpath scanning.
//It is a kind of interceptor that surrounds the logic in our Controllers and allows us to apply some common logic to them.

@RestControllerAdvice //@RestControllerAdvice = @ControllerAdvice + @ResponseBody
public class GlobalViolationExceptionHandler implements HandlerInterceptor, RequestBodyAdvice { //implements RequestBodyAdvice = возможность получить содержимое тела запроса
//При спробі просто отримати тіло запиту з HttpServletRequest/WebRequest і т.д. виникають проблеми як тут(https://coderoad.ru/43502332/%D0%9A%D0%B0%D0%BA-%D0%BF%D0%BE%D0%BB%D1%83%D1%87%D0%B8%D1%82%D1%8C-RequestBody-%D0%B2-ExceptionHandler-Spring-REST)
//... тому городим такий огород з зберіганням данних запиту використовуючиHandlerInterceptor, RequestBodyAdvice

    //Используем перехватчик(HandlerInterceptor), что бы получить другую информацию о запросе, такую как метод HTTP и параметры запроса.
    //Зафиксируем всю эту информацию запроса для сообщения об ошибках в переменной ThreadLocal, которую будем очищать на крючке afterCompletion в том же перехватчике.
//Класс java.lang.ThreadLocal<T> используется для хранения переменных, которые должны быть доступны для всего потока. Фактически это нечто вроде ещё одной области видимости переменных. Класс ThreadLocal  имеет методы get  и set, которые позволяют получить текущее значение и установить новое значение.
//Обычно экземпляры ThreadLocal  объявляются как приватные статические переменные в классе. Каждый поток получает из метода get своё значение и устанавливает через set тоже своё значение, изолированное от других потоков.
    private static final ThreadLocal<GlobalViolationExceptionHandler> requestInfoThreadLocal = new ThreadLocal<>();

    private String method;
    private String body;
    private String queryString;
    private String ip;
    private String user;
    private String referrer;
    private String url;

    @Autowired
    private ReplyMessageService replyMessageService;
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private CreateButtonService buttonService;
    @Autowired
    private ZhannaFedorukTelegramBot telegramBot;

    //    Rest Controller Advice’s methods (annotated with @ExceptionHandler) are shared globally across multiple @Controller components
//    to capture exceptions and translate them to HTTP responses. The @ExceptionHandler annotation indicates which type of Exception we want to handle.
//    The exception instance and the request will be injected via method arguments.
    @ExceptionHandler(ConstraintViolationException.class)
    public BotApiMethod<?> onConstraintViolationException(ConstraintViolationException e) throws IOException {
        String payloadRequest = requestInfoThreadLocal.get().body;

        //get chat_id
        Pattern pattern = Pattern.compile("(?<=Chat\\(id=)\\d+?(?=,)");
        Matcher matcher = pattern.matcher(payloadRequest);
        String chatId = null;
        if (matcher.find()) {
            chatId = payloadRequest.substring(matcher.start(), matcher.end());
        }

        //create text about captured exceptions
//        String errorsMsgFromValidation = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining()); //it could be a part of LocalValidatorFactoryBean, but it return "{validation.exception_phone}"
        String errorsMsgFromValidation = e.getConstraintViolations().stream().map(constraintViolation -> replyMessageService.getReplyText(constraintViolation.getMessage())).collect(Collectors.joining());
        assert chatId != null;

        String replyText = replyMessageService.getReplyText("validation.exception_facade", Emoji.WARNING, errorsMsgFromValidation);
        SendMessage errorMessage = new SendMessage(chatId, replyText);

        telegramBot.sendSeveralAnswers(0, errorMessage);

        SendMessage replyMessage = replyMessageService.getReplyMessage(Long.parseLong(chatId), "consultation.registration_messenger_data");
        replyMessage.setReplyMarkup(buttonService.createButtons4ps2x2("Telegram", "Viber", "WhatsApp", "Facebook"));
        userDataCache.setUsersCurrentBotState(Long.parseLong(chatId), BotState.ASK_CONSULTATION_MESSENGER);

        return replyMessage;
    }

    public static GlobalViolationExceptionHandler get() {
        GlobalViolationExceptionHandler requestInfo = requestInfoThreadLocal.get();
        if (requestInfo == null) {
            requestInfo = new GlobalViolationExceptionHandler();
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
        GlobalViolationExceptionHandler requestInfo = requestInfoThreadLocal.get();
        if (requestInfo == null) {
            requestInfo = new GlobalViolationExceptionHandler();
        }
        requestInfo.setInfoFromRequest(request);
        requestInfoThreadLocal.set(requestInfo);
    }

    private static void clear() {
        requestInfoThreadLocal.remove();
    }

    private static void setBodyInThreadLocal(String body) {
        GlobalViolationExceptionHandler requestInfo = get();
        requestInfo.setBody(body);
        setRequestInfo(requestInfo);
    }

    private static void setRequestInfo(GlobalViolationExceptionHandler requestInfo) {
        requestInfoThreadLocal.set(requestInfo);
    }

    // Implementation of HandlerInterceptor to capture the request info (except body) and be able to add it to the report in case of an error

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        GlobalViolationExceptionHandler.setInfoFrom(request);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        GlobalViolationExceptionHandler.clear();
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
        GlobalViolationExceptionHandler.setBodyInThreadLocal(body.toString());
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
