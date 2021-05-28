package ua.antonfedoruk.zhfed_tgbot.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.antonfedoruk.zhfed_tgbot.service.ScraperService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// In-memory cache
// Defines which news should be send to user
@Component
@Slf4j
public class NewsDataCache implements NewsCache {
    // key: user chat id,   value: the last shown news headline
    private Map<String, String> shownToUser = new HashMap<>();
    // key: headline,   value: news
    private Map<String, ScraperService.News> newsCache = new LinkedHashMap<>();

    private ScraperService scraperService;

    public NewsDataCache(ScraperService scraperService) {
        this.scraperService = scraperService;
        loadNews();
    }

    @Override
    public ScraperService.News getNewsPostForUserWithChatId(String usersChatId) throws NullPointerException {
        if (shownToUser.containsKey(usersChatId)) {
            log.info("(" + usersChatId + ") This user have obtained news previously!");
            String lastHeadline = shownToUser.get(usersChatId);

            final String[] nextHeadline = new String[1];
            final boolean[] isNext = {false};
            final ScraperService.News[] res = new ScraperService.News[1];

            newsCache.forEach((headline, news) -> {
                if (headline.equals(lastHeadline)) {
                    isNext[0] = true;
                } else if (isNext[0]) {
                    nextHeadline[0] = headline;
                    res[0] = news;
                    isNext[0] = false;
                }
            });

            shownToUser.put(usersChatId, nextHeadline[0]);

            if (res[0] == null) {
                throw new NullPointerException("No more news:(");
            }

            return res[0];
        } else {
            log.info("(" + usersChatId + ") First attempt to obtained news");
            Map.Entry<String, ScraperService.News> firstNews = newsCache.entrySet().iterator().next();
            shownToUser.put(usersChatId, firstNews.getKey());

            return firstNews.getValue();
        }
    }

    private void loadNews() {
        log.info("Loading news to cache . . .");
        scraperService.getNews().forEach(this::saveNews);
        log.info("News loading completed!");
    }

    private void saveNews(ScraperService.News news) {
        if (!newsCache.containsKey(news.getHeadline())) {
            newsCache.put(news.getHeadline(), news);
        }
    }
}
