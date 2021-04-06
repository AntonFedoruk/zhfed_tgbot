package ua.antonfedoruk.zhfed_tgbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateButtonService {

    public InlineKeyboardMarkup createButton(String buttonText) {
        //Создаем обьект разметки клавиатуры:
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        //Теперь выстраиваем положение кнопок.

        //Создаем обьекты InlineKeyboardButton, у которых есть 2 параметра:
        //Текст (Что будет написано на самой кнопке) и CallBackData (Что будет отсылатся серверу при нажатии на кнопку).
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setCallbackData("Button \"" + buttonText + "\" has been pressed");

        //Добавляем его в список, таким образом создавая ряд.
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);

        //После этого нам нужно 'обьеденить ряды'(если у нас их несколько), поэтому создаем список рядов.
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        //Теперь мы можем установить кнопку в обьект разметки клавиатуры.
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
