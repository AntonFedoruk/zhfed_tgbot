package ua.antonfedoruk.zhfed_tgbot;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.antonfedoruk.zhfed_tgbot.botapi.TelegramFacade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

// This class describes our variant of TelegramWebhookBot bean, that we will declare in config files.
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZhannaFedorukTelegramBot extends TelegramWebhookBot {
    String webHookPath;
    String botUserName;
    String botToken;

    TelegramFacade telegramFacade;

    public ZhannaFedorukTelegramBot(DefaultBotOptions options, TelegramFacade telegramFacade) {
        super(options);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @SneakyThrows
    public void sendSeveralAnswers(long pauseBetweenAnswersInSeconds, BotApiMethod<?>...methods) {
        for ( BotApiMethod<?> method : methods) {
            if (!method.getMethod().equals("sendChatAction")) {
                Thread.sleep(pauseBetweenAnswersInSeconds*1000);
            }
            execute(method);
        }
    }

    //Use this method to send photos.
    @SneakyThrows
    public void sendPhoto(long chatId, String imageCaption, String imagePath) {
        // робочий варіант (але не для jar)
//        File image = ResourceUtils.getFile("classpath:" + imagePath);
//        SendPhoto sendPhoto = new SendPhoto();
//        sendPhoto.setPhoto(new InputFile(image));
//        sendPhoto.setChatId(Long.toString(chatId));
//        sendPhoto.setCaption(imageCaption);
//        execute(sendPhoto);


        // solution  | stackoverflow
        //          \/

        //resource.getFile() expects the resource itself to be available on the file system, i.e. it can't be nested inside a jar file.
        // This is why it works when you run your application in STS but doesn't work once you've built your application and run it from
        // the executable jar. Rather than using getFile() to access the resource's contents, I'd recommend using getInputStream() instead.
        // That'll allow you to read the resource's content regardless of where it's located.
        ClassLoader classLoader = getClass().getClassLoader();

        File image = null;

        try (InputStream inputStream = classLoader.getResourceAsStream(imagePath)) {

            final Path path = Files.createTempFile("tempImg_" + UUID.randomUUID().toString(), ".jpg");

            if (inputStream != null) {
                //Writing data here
                byte[] buf = inputStream.readAllBytes();
                Files.write(path, buf);

                //For appending to the existing file
                //Files.write(path, buf, StandardOpenOption.APPEND);
            }

            image = new File(path.toUri());

            //Delete file on exit (the file will be deleted when the program exits.)
            path.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(image));
        sendPhoto.setChatId(Long.toString(chatId));
        sendPhoto.setCaption(imageCaption);
        execute(sendPhoto);
    }
}