package ua.antonfedoruk.zhfed_tgbot.validation;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/*
 Глобальный ControllerAdvice, который обрабатывает все ConstraintViolationExceptions, которые пробрасываются до уровня контроллера.

 Здесь информацию о нарушениях из исключений переводится в нашу структуру данных ValidationErrorResponse.
 */
@ControllerAdvice
public class ErrorHandlingControllerAdvice {
    private ReplyMessageService replyMessageService;

    public ErrorHandlingControllerAdvice(ReplyMessageService replyMessageService) {
        this.replyMessageService = replyMessageService;
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BotApiMethod<?> onConstraintValidationException(ConstraintViolationException e, Update update) {
        Long chatId = update.getMessage().getChatId();

        final List<Violation> violations = e.getConstraintViolations().stream() //Returns the set of constraint violations reported during a validation.
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(violations);

        return replyMessageService.getReplyMessage(chatId, validationErrorResponse.getViolations().iterator().next().getMessage());
    }


//    Чтобы отлавливать ошибки валидации и для тел запросов, мы также будем перехватывать и MethodArgumentNotValidExceptions
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidExceptions(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE;
    }
}
