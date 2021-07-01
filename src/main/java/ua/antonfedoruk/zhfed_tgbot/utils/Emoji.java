package ua.antonfedoruk.zhfed_tgbot.utils;

import com.vdurmont.emoji.EmojiParser;

public enum Emoji {
    MONEY(":dollar:"),
    PC(":desktop_computer:"),
    CALLING(":calling:"),
    ARROW_DOWN(":arrow_down:"),
    PEN(":lower_left_fountain_pen:"),
    WINK(":wink:"),
    POINT_DOWN(":point_down:"),
    CLAP(":clap:"),
    LIKE(":thumbsup:"),
    NOTES(":memo:"),
    DOT(":small_blue_diamond:"),
    BLUSH(":blush:"),
    GIFT(":gift:"),
    BOOK(":green_book:"),
    WOMAN_TEACHER(":woman_teacher:"),
    SEND_LETTER(":incoming_envelope:"),
    INBOX_TRAY(":inbox_tray:"),
    WARNING(":warning:"),
    FLAG_UA(":ua:"),
    FLAG_EN(":us:"),
    FLAG_RU(":ru:"),
    GLOBE(":globe_with_meridians:");

    private String name;

    Emoji(String name) {
        this.name = EmojiParser.parseToUnicode(name);
    }

    @Override
    public String toString() {
        return name;
    }
}