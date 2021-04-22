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

    public InlineKeyboardMarkup createButtonWithUrl(String buttonText, String url) {
        //Создаем обьект разметки клавиатуры:
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        //Теперь выстраиваем положение кнопок.

        //Создаем обьекты InlineKeyboardButton, у которых есть 2 параметра:
        //Текст (Что будет написано на самой кнопке) и CallBackData (Что будет отсылатся серверу при нажатии на кнопку).
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setUrl(url);
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

    public InlineKeyboardMarkup createButtons4ps2x2(String buttonText11,String buttonText12,String buttonText21,String buttonText22) {
        //Создаем обьект разметки клавиатуры:
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        //Теперь выстраиваем положение кнопок.

        //Создаем обьекты InlineKeyboardButton, у которых есть 2 параметра:
        //Текст (Что будет написано на самой кнопке) и CallBackData (Что будет отсылатся серверу при нажатии на кнопку).
        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText(buttonText11);
        button11.setCallbackData("Button \"" + buttonText11 + "\" has been pressed");

        InlineKeyboardButton button12 = new InlineKeyboardButton();
        button12.setText(buttonText12);
        button12.setCallbackData("Button \"" + buttonText12 + "\" has been pressed");

        InlineKeyboardButton button21 = new InlineKeyboardButton();
        button21.setText(buttonText21);
        button21.setCallbackData("Button \"" + buttonText21 + "\" has been pressed");

        InlineKeyboardButton button22 = new InlineKeyboardButton();
        button22.setText(buttonText22);
        button22.setCallbackData("Button \"" + buttonText22 + "\" has been pressed");

        //Добавляем его в список, таким образом создавая ряд.
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(button11);
        keyboardButtonsRow1.add(button12);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(button21);
        keyboardButtonsRow2.add(button22);

        //После этого нам нужно 'обьеденить ряды'(если у нас их несколько), поэтому создаем список рядов.
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        //Теперь мы можем установить кнопку в обьект разметки клавиатуры.
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
