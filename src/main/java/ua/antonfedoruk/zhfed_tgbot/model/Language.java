package ua.antonfedoruk.zhfed_tgbot.model;

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

    public String getLanguageTag2Letters() {
        return getLanguageTag().substring(0, 2);
    }
}
