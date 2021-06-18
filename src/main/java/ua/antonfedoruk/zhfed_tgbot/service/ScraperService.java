package ua.antonfedoruk.zhfed_tgbot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This service is used to capture news from 'inCruises blog' web(HTML) page.
// tutorial for Jsoup: https://www.baeldung.com/java-with-jsoup
@Service
@Slf4j
public class ScraperService {

    @Value("${news.prefix_for_href_attr}")
    private String hrefPrefix;

    @Value("${news.posts_url}")
    private String newsUrl;

    //WebDriver represents the browser
    private final ChromeDriver driver;

    public ScraperService(ChromeDriver driver) {
        this.driver = driver;
    }

    @Getter
    public class News {
        private String headline;
        private String text;
        private String imageLink;
        private String link;

        public News(String headline, String text, String imageLink, String link) {
            this.headline = headline;
            this.text = text;
            this.imageLink = imageLink;
            this.link = link;
        }
    }

    //get bunch of news from site
    public List<News> getNewsWithSpecificLanguage(String lang) { // lang: "ru" / "uk" / "en"
        //      Browser navigation
        // Open your website:
        // convenient way
        driver.get(newsUrl); //longer way: driver.navigate().to(newsUrl);

        WebDriverWait wait = new WebDriverWait(driver, 10);// Explicit wait
        // We wait for the ajax call to fire and to load the response into the page
        wait.until(webDriver -> webDriver.findElement(By.cssSelector(".row-cards")));

        //WebElement represents a particular DOM node (a control, e.g. a link or input field, etc.)
//        WebElement language = driver.findElement(By.name("language"));
        WebElement language = driver.findElementByCssSelector("#selectForm > option[value='" + lang + "']");
        language.click();
//        Select drpLanguage = new Select(driver.findElementByName("language"));
//        drpLanguage.selectByValue("ru");
//        drpLanguage.getWrappedElement().click();
//        language.click();

        wait.until(webDriver -> webDriver.findElement(By.cssSelector(".row-cards")));

        String pageSource = driver.getPageSource();

        // initial data from web page
        Document page = Jsoup.parse(pageSource);

        Element postsBlock = page.select(".row-cards").first();

        Elements posts = postsBlock.select("#post-container");

        List<News> news = new ArrayList<>(); //news.size(): 8
        for (Element el : posts) {
            news.add(parseElementToNews(el));
        }

        return news;
    }

    private News parseElementToNews(Element element) {
        Element linkElement = element.select("h4 > a").first();

        String headlines = linkElement.text();

        String link = hrefPrefix + linkElement.attr("href");

        String text = element.select("div.p-2").first().text();

        String attrWithImgUrlInside = element.select("a div").first().attr("style");

        // input:        background: url("https://i.imgur.com/pdZGatf.jpg") center center / cover no-repeat; padding-top: 70%;
        Pattern pattern = Pattern.compile("(?<=url\\(\")https://.+?(?=\"\\))");
        // output:      https://i.imgur.com/pdZGatf.jpg
        Matcher matcher = pattern.matcher(attrWithImgUrlInside);
        String imgLink = null;
        if (matcher.find()) {
            imgLink = attrWithImgUrlInside.substring(matcher.start(), matcher.end());
        }

        return new News(headlines, text, imgLink, link);
    }
}