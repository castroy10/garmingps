package ru.castroy10.garmingps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.castroy10.garmingps.bot.CoordinateBot;

@SpringBootApplication
@Slf4j
public class GarmingpsApplication {

    public static void main(final String[] args) throws TelegramApiException {
        final ConfigurableApplicationContext context = SpringApplication.run(GarmingpsApplication.class, args);
        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        final CoordinateBot coordinateBot = context.getBean(CoordinateBot.class);
        telegramBotsApi.registerBot(coordinateBot);
        log.info("Bot started");
    }

}
