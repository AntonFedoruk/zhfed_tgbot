package ua.antonfedoruk.zhfed_tgbot.cache;

import ua.antonfedoruk.zhfed_tgbot.service.ScraperService;

public interface NewsCache {
    ScraperService.News getNewsPostForUserWithChatId(String usersChatId) throws NullPointerException;
}
