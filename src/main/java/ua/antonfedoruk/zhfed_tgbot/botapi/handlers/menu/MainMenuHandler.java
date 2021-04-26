package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.service.MainMenuService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

// this class should be used when we want to advise the user to use the main menu

@Component
public class MainMenuHandler implements InputMessageHandler {
    private ReplyMessageService replyMessageService;
    private MainMenuService mainMenuService;

    public MainMenuHandler(ReplyMessageService replyMessageService, MainMenuService mainMenuService) {
        this.replyMessageService = replyMessageService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
       return mainMenuService.getMainMenuMessage(String.valueOf(message.getChatId()),
               replyMessageService.getReplyText("main_menu.use_main_menu"));
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.SHOW_MAIN_MENU;
    }
}
