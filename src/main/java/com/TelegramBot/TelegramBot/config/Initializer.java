package com.TelegramBot.TelegramBot.config;

import com.TelegramBot.TelegramBot.service.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class Initializer {
    @Autowired
    TelegramBotService teLeBotService;

    @EventListener({ContextRefreshedEvent.class})
    public void initBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(teLeBotService);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
