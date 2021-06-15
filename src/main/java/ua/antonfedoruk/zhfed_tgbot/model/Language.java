package ua.antonfedoruk.zhfed_tgbot.model;

import lombok.Setter;

public enum Language {
    ENGLISH("en-En"),
    RUSSIA("ru-RU"),
    UKRAINE("ua-UA");

    Language(String languageTag) {
        this.languageTag = languageTag;
    }

    private final String languageTag;

    public String getLanguageTag() {
        return languageTag;
    }
}
