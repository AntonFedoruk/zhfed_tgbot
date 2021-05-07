package ua.antonfedoruk.zhfed_tgbot.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/*
Когда проверка не удается, лучше вернуть клиенту понятное сообщение об ошибке. Для этого необходимо вернуть структуру
данных с сообщением об ошибке для каждой проверки, которая не прошла валидацию.

Сначала нужно определить эту структуру данных. Назовем ее ValidationErrorResponse и она содержит список объектов Violation:
 */

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final List<Violation> violations;
}