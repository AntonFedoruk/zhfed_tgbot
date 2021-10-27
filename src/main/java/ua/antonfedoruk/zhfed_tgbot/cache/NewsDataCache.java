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
    private Map<String, String> usersWithLastShownNewsHeadline = new HashMap<>();
    // key: headline,   value: news
//    private Map<String, ScraperService.News> newsCache = new LinkedHashMap<>();

    // key: language,  value: Map<headline, news>
    private Map<String, Map<String, ScraperService.News>> newsCacheForAllLanguages = new LinkedHashMap<>();

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

        if (usersWithLastShownNewsHeadline.containsKey(usersChatId)) {
            String lastShownNewsHeadline = usersWithLastShownNewsHeadline.get(usersChatId);
            log.info("(" + usersChatId + ") This user obtained news previously.");

            String[] nextHeadline = new String[1];
            boolean[] takeNextOne = {false};
            ScraperService.News[] resultingNews = new ScraperService.News[1];

//            newsCache.forEach((headline, news) -> {
            newsCacheForAllLanguages.get(userlanguageTag).forEach((newsHeadline, news) -> {
                if (takeNextOne[0]) {
                    nextHeadline[0] = newsHeadline;
                    resultingNews[0] = news;
                    takeNextOne[0] = false;
                } else if (newsHeadline.equals(lastShownNewsHeadline)) {
                    takeNextOne[0] = true;
                }
            });

            usersWithLastShownNewsHeadline.put(usersChatId, nextHeadline[0]);

            if (takeNextOne[0]) {
                throw new NullPointerException("No more news:(");
            }

            if (resultingNews[0] == null) {

                log.info("(" + usersChatId + ") it seems like user have changed lang; show all news with correct lang from start");
                Map.Entry<String, ScraperService.News> firstNews = newsCacheForAllLanguages.get(userlanguageTag).entrySet().iterator().next();
                usersWithLastShownNewsHeadline.put(usersChatId, firstNews.getKey());

                return firstNews.getValue();
            }

            log.info("User" + "(" + usersChatId + ")" + " should obtain news with header: " + nextHeadline);

            return resultingNews[0];
        } else {
            log.info("(" + usersChatId + ") First attempt to obtained news");
//            Map.Entry<String, ScraperService.News> firstNews = newsCache.entrySet().iterator().next();
            Map.Entry<String, ScraperService.News> firstNews = newsCacheForAllLanguages.get(userlanguageTag).entrySet().iterator().next();
            usersWithLastShownNewsHeadline.put(usersChatId, firstNews.getKey());

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

//        System.out.println("News hash: \n*********");
//        newsCacheForAllLanguages.entrySet().forEach(
//                entry -> entry.getValue().forEach(
//                        (header, news) -> System.out.println(header)
//                )
//        );
//        System.out.println("*********");
    }

    private void saveNews(ScraperService.News news, String lang) {
//        if (!newsCache.containsKey(news.getHeadline())) {
//            newsCache.put(news.getHeadline(), news);
//        }
        if (lang.equals("uk")) {
            lang = "ua";
        }

        if (!newsCacheForAllLanguages.containsKey(lang)) {
            LinkedHashMap<String, ScraperService.News> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put(news.getHeadline(), news);
            newsCacheForAllLanguages.put(lang, linkedHashMap);

        } else {
            Map<String, ScraperService.News> map = newsCacheForAllLanguages.get(lang);
            if (!map.containsKey(news.getHeadline())) {
                map.put(news.getHeadline(), news);
            }
        }
    }
}
