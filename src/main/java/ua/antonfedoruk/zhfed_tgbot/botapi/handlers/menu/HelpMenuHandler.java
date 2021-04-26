package ua.antonfedoruk.zhfed_tgbot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.antonfedoruk.zhfed_tgbot.botapi.BotState;
import ua.antonfedoruk.zhfed_tgbot.botapi.handlers.InputMessageHandler;
import ua.antonfedoruk.zhfed_tgbot.service.MainMenuService;
import ua.antonfedoruk.zhfed_tgbot.service.ReplyMessageService;

@Component
public class HelpMenuHandler implements InputMessageHandler {
    private ReplyMessageService replyMessageService;
    private MainMenuService mainMenuService;

    public HelpMenuHandler(ReplyMessageService replyMessageService, MainMenuService mainMenuService) {
        this.replyMessageService = replyMessageService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
       return mainMenuService.getMainMenuMessage(String.valueOf(message.getChatId()),
               replyMessageService.getReplyText("main_menu.use_help_menu"));
    }

    @Override
    public BotState getHandlersBotState() {
        return BotState.SHOW_HELP_MENU;
    }
}