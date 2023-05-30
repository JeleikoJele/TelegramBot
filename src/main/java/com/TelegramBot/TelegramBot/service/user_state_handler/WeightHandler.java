package com.TelegramBot.TelegramBot.service.user_state_handler;

import com.TelegramBot.TelegramBot.config.ConfigBot;
import com.TelegramBot.TelegramBot.enums.State;
import com.TelegramBot.TelegramBot.model.UserState;
import com.TelegramBot.TelegramBot.repository.UserStateRepository;
import com.TelegramBot.TelegramBot.service.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Service
@Order(value = 1)
public class WeightHandler extends MessageUtils implements UserStateHandler {

    @Override
    public void handleUpdate(AbsSender absSender, Update update, UserState userState,
                             long chatId, UserStateRepository userStateRepository, int messageId, ConfigBot configBot) {

        try {

            if (Double.parseDouble(update.getMessage().getText()) <= 0) {
                sendAudio(absSender, configBot, chatId, messageId, update);
                sendMessage(absSender, chatId, "Ай ай ай, прекращай баловаться и укажи свой вес!", messageId, update);
                userState.setNextState(State.WEIGHT_STEP);
            } else {
                userState.setWeight(Double.parseDouble(update.getMessage().getText()));
                userState.setNextState(State.PERCENT_STEP);
                userStateRepository.save(userState);

                sendMessage(absSender, chatId, State.PERCENT_STEP.toString(), messageId, update);
            }
        } catch (Exception e) {
            warnUser(absSender, State.WEIGHT_STEP, userState, chatId);
            log.error("Error - " + e.getMessage());
        }
    }
}
