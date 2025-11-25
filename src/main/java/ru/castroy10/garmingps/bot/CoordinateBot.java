package ru.castroy10.garmingps.bot;

import jakarta.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.castroy10.garmingps.model.Coordinate;
import ru.castroy10.garmingps.service.GpsCreatorService;
import ru.castroy10.garmingps.service.ParseCoordinateService;

@Component
@Slf4j
public class CoordinateBot extends TelegramLongPollingBot {

    private final String botName;
    private final ParseCoordinateService parseCoordinateService;
    private final GpsCreatorService gpsCreatorService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public CoordinateBot(@Value("${telegram.bot.name}") final String botName,
                         @Value("${telegram.bot.token}") final String botToken,
                         final ParseCoordinateService parseCoordinateService,
                         final GpsCreatorService gpsCreatorService) {
        super(botToken);
        this.botName = botName;
        this.parseCoordinateService = parseCoordinateService;
        this.gpsCreatorService = gpsCreatorService;
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (update == null || !update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        final String text = update.getMessage().getText();
        final Long chatId = update.getMessage().getChatId();
        if (greeting(chatId, text)) {
            return;
        }
        executorService.submit(() -> {
            try {
                final List<Coordinate> coordinateList = parseCoordinateService.parseCoordinateFromText(text);
                final ResponseEntity<byte[]> gpxTrack = gpsCreatorService.createGpxTrack(coordinateList);
                sendFile(chatId, gpxTrack.getBody());
            } catch (final Exception e) {
                log.error("Unexpected error while processing chat {}", chatId, e);
                sendMessage(chatId, "Сервер вернул ошибку:\n" + e.getMessage());
            }
        });
    }

    private void sendFile(final Long chatId, final byte[] body) {
        final InputStream inputStream = new ByteArrayInputStream(body);
        final InputFile inputFile = new InputFile(inputStream, "track_from_text.gpx");
        final SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(chatId));
        sendDocument.setDocument(inputFile);
        sendDocument.setCaption("Файл успешно создан");
        try {
            execute(sendDocument);
        } catch (final TelegramApiException e) {
            log.error("Telegram error sending file", e);
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(final Long chatId, final String text) {
        final String chatIdStr = String.valueOf(chatId);
        final SendMessage sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (final TelegramApiException e) {
            log.error("Telegram error sending message", e);
        }
    }

    private boolean greeting(final Long chatId, final String text) {
        if (text.equalsIgnoreCase("/start")) {
            sendMessage(chatId, "Это бот для создания Garmin трека из координат.\n");
            return true;
        }
        return false;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @PreDestroy
    @SneakyThrows
    public void destroy() {
        executorService.shutdown();
        final boolean bool = executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

}
