package ua.antonfedoruk.zhfed_tgbot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Emoji {
    MONEY(EmojiParser.parseToUnicode(":dollar:")),
    PC(EmojiParser.parseToUnicode(":desktop_computer:")),
    CALLING(EmojiParser.parseToUnicode(":calling:")),
    ARROW_DOWN(EmojiParser.parseToUnicode(":arrow_down:")),
    PEN(EmojiParser.parseToUnicode(":lower_left_fountain_pen:")),
    WINK(EmojiParser.parseToUnicode(":wink:")),
    POINT_DOWN(EmojiParser.parseToUnicode(":point_down:")),
    CLAP(EmojiParser.parseToUnicode(":clap:")),
    LIKE(EmojiParser.parseToUnicode(":thumbsup:")),
    NOTES(EmojiParser.parseToUnicode(":memo:")),
    DOT(EmojiParser.parseToUnicode(":small_blue_diamond:")),
    BLUSH(EmojiParser.parseToUnicode(":blush:")),
    GIFT(EmojiParser.parseToUnicode(":gift:")),
    BOOK(EmojiParser.parseToUnicode(":green_book:")),
    WOMAN_TEACHER(EmojiParser.parseToUnicode(":woman_teacher:")),
    SEND_LETTER(EmojiParser.parseToUnicode(":incoming_envelope:")),
    INBOX_TRAY(EmojiParser.parseToUnicode(":inbox_tray:"));

    private String name;

    @Override
    public String toString() {
        return name;
    }
}
