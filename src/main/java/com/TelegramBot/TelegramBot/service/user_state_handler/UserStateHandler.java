package com.TelegramBot.TelegramBot.service.user_state_handler;

import com.TelegramBot.TelegramBot.config.ConfigBot;
import com.TelegramBot.TelegramBot.model.UserState;
import com.TelegramBot.TelegramBot.repository.UserStateRepository;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface UserStateHandler {
    void handleUpdate(AbsSender absSender, Update update, UserState userState,
                      long chatId, UserStateRepository userStateRepository, int messageId, ConfigBot configBot);
}
