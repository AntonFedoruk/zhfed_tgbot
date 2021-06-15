package ua.antonfedoruk.zhfed_tgbot.config;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class SeleniumConfiguration {

    @PostConstruct
    void init() {
        System.setProperty("webdriver.chrome.driver", "/home/anton/chromedrivers/chromedriver_91_linux64/chromedriver");
    }

    @PreDestroy
    void close() {
//    driver.close();//Close the tab or window
      driver().quit(); //Quitting the browser at the end of a session. When you are finished with the browser session you should call quit, instead of close.
//        Quit will:
//        - Close all the windows and tabs associated with that WebDriver session
//        - Close the browser process
//        - Close the background driver process
//        - Notify Selenium Grid that the browser is no longer in use so it can be used by another session (if you are using Selenium Grid)
    }

    @Bean
    public ChromeDriver driver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");// Bypass OS security model, MUST BE THE VERY FIRST OPTION
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-dev-shm-usage");// overcome limited resource problems
        // chromeOptions.addArguments("--window-size=1024, 768"); //or: driver.manage().window().setSize(new Dimension(1024, 768));
        chromeOptions.addArguments("--start-maximized"); //or: driver.manage().window().maximize();
        // chromeOptions.addArguments(String.format("--lang=%s", locale.substring(0, 2)));//The language file that we want to try to open. Of the form language[-country] where language is the 2 letter code from ISO-639.
        //create chrome instance
        return new ChromeDriver(chromeOptions);
    }
}
