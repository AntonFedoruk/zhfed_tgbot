package ua.antonfedoruk.zhfed_tgbot.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.antonfedoruk.zhfed_tgbot.model.Language;
import ua.antonfedoruk.zhfed_tgbot.service.ScraperService;
import ua.antonfedoruk.zhfed_tgbot.service.UsersProfileDataService;

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
//    private Map<String, ScraperService.News> newsCache = new LinkedHashMap<>();


    // key: language,  value Map<headline, news>
    private Map<String, Map<String, ScraperService.News>> allNewsCache = new HashMap<>();

    private final UsersProfileDataService usersProfileDataService;


    private ScraperService scraperService;

    public NewsDataCache(UsersProfileDataService usersProfileDataService, ScraperService scraperService) {
        this.usersProfileDataService = usersProfileDataService;
        this.scraperService = scraperService;
        loadNews();
    }

    @Override
    public ScraperService.News getNewsPostForUserWithChatId(String usersChatId) throws NullPointerException {
        Language userLanguage = usersProfileDataService.getUserLanguage(Long.parseLong(usersChatId));
        String userlanguageTag = userLanguage.getLanguageTag2Letters();

        if (shownToUser.containsKey(usersChatId)) {
            log.info("(" + usersChatId + ") This user have obtained news previously!");
            String lastHeadline = shownToUser.get(usersChatId);

            final String[] nextHeadline = new String[1];
            final boolean[] isNext = {false};
            final ScraperService.News[] res = new ScraperService.News[1];

//            newsCache.forEach((headline, news) -> {
            allNewsCache.get(userlanguageTag).forEach((headline, news) -> {
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
//            Map.Entry<String, ScraperService.News> firstNews = newsCache.entrySet().iterator().next();
            Map.Entry<String, ScraperService.News> firstNews = allNewsCache.get(userlanguageTag).entrySet().iterator().next();
            shownToUser.put(usersChatId, firstNews.getKey());

            return firstNews.getValue();
        }
    }

    private void loadNews() {
        log.info("Loading news to cache . . .");
//        scraperService.getNewsWithSpecificLanguage("ru").forEach(this::saveNews);
        scraperService.getNewsWithSpecificLanguage("ru").forEach(news -> saveNews(news, "ru"));
        scraperService.getNewsWithSpecificLanguage("uk").forEach(news -> saveNews(news, "uk"));
        scraperService.getNewsWithSpecificLanguage("en").forEach(news -> saveNews(news, "en"));
        log.info("News loading completed!");
    }

    private void saveNews(ScraperService.News news, String lang) {
//        if (!newsCache.containsKey(news.getHeadline())) {
//            newsCache.put(news.getHeadline(), news);
//        }
        if (!allNewsCache.containsKey(lang)) {
            LinkedHashMap<String, ScraperService.News> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put(news.getHeadline(), news);
            allNewsCache.put(lang, linkedHashMap);

        } else {
            Map<String, ScraperService.News> map = allNewsCache.get(lang);
            if (!map.containsKey(news.getHeadline())) {
                map.put(news.getHeadline(), news);
            }
        }
    }
}
