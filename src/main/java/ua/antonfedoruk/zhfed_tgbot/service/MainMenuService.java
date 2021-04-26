package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainMenuService {
    private LocaleMessageService localeMessageService;

    public MainMenuService(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    public SendMessage getMainMenuMessage(final String chatId, final String text) {
        //ReplyKeyboardMarkup представляет клавиатуру с опциями ответа
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();
        return createMessageWithKeyboard(chatId, text, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        //Опционально. Этот параметр нужен, чтобы показывать клавиатуру только определённым пользователям. Цели: 1) пользователи, которые были @упомянуты в поле text объекта Message; 2) если сообщения бота является ответом (содержит поле reply_to_message_id), авторы этого сообщения.
        //Пример: Пользователь отправляет запрос на смену языка бота. Бот отправляет клавиатуру со списком языков, видимую только этому пользователю.
        replyKeyboardMarkup.setSelective(true);

        //Опционально. Указывает клиенту подогнать высоту клавиатуры под количество кнопок (сделать её меньше, если кнопок мало). По умолчанию False, то есть клавиатура всегда такого же размера, как и стандартная клавиатура устройства.
        replyKeyboardMarkup.setResizeKeyboard(true);

        //Опционально. Указывает клиенту скрыть клавиатуру после использования (после нажатия на кнопку). Её по-прежнему можно будет открыть через иконку в поле ввода сообщения. По умолчанию False.
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        //підготовим кнопки для цього меню
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        row1.add(new KeyboardButton(localeMessageService.getMessage("main_menu.button_hello")));
        row2.add(new KeyboardButton(localeMessageService.getMessage("main_menu.button_consultation")));
        row3.add(new KeyboardButton(localeMessageService.getMessage("main_menu.button_news")));
        row3.add(new KeyboardButton(localeMessageService.getMessage("main_menu.button_help")));
        row4.add(new KeyboardButton(localeMessageService.getMessage("main_menu.button_exit")));
        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);
        keyboardRows.add(row4);

        //Массив рядов кнопок, каждый из которых является массивом объектов KeyboardButton
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }


    private SendMessage createMessageWithKeyboard(final String chatId, String text, final ReplyKeyboardMarkup replyKeyboardMarkup) {

        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }

        return sendMessage;
    }
}
