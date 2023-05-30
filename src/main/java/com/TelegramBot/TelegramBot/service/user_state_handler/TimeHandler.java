package com.TelegramBot.TelegramBot.service.user_state_handler;

import com.TelegramBot.TelegramBot.config.ConfigBot;
import com.TelegramBot.TelegramBot.enums.State;
import com.TelegramBot.TelegramBot.model.UserState;
import com.TelegramBot.TelegramBot.repository.UserStateRepository;
import com.TelegramBot.TelegramBot.service.BoozeCalculator;
import com.TelegramBot.TelegramBot.service.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.text.DecimalFormat;

@Slf4j
@Service
@Order(value = 3)
public class TimeHandler extends MessageUtils implements UserStateHandler {

    @Override
    public void handleUpdate(AbsSender absSender, Update update, UserState userState,
                             long chatId, UserStateRepository userStateRepository, int messageId, ConfigBot configBot) {

        try {
            if (Double.parseDouble(update.getMessage().getText()) <= 0) {
                sendAudio(absSender, configBot, chatId, messageId, update);
                sendMessage(absSender, chatId, "Ай ай ай, прекращай баловаться и укажи время!", messageId, update);
                userState.setNextState(State.TIME_STEP);
            } else {
                userState.setTime(Double.parseDouble(update.getMessage().getText()));
                userState.setNextState(State.NULL_STEP);
                userStateRepository.save(userState);
                calculateResult(update, userState, messageId, absSender, configBot);
            }
        } catch (Exception e) {
            warnUser(absSender, State.TIME_STEP, userState, chatId);

            log.error("Error - " + e.getMessage());
        }
    }

    private void calculateResult(Update update, UserState userState, int messageId, AbsSender absSender, ConfigBot configBot) {
        long chatId = update.getMessage().getChatId();
        double result;
        BoozeCalculator boozeCalculator = new BoozeCalculator();

        if (userState.getTime() == 1) {
            result = boozeCalculator.calculateBaseAmount(userState) / 1.5;
        } else {
            result = boozeCalculator.calculateBaseAmount(userState) + boozeCalculator.calculatePerHourAmount(userState);
        }

        if(result > 0) {
            String formattedResult = new DecimalFormat("#0.00").format(result);
            String text = "Вам необходимо " + formattedResult + " литров";

            sendMessage(absSender, chatId, text, messageId, update);
        } else {
            sendAudio(absSender, configBot, chatId, messageId, update);
            sendMessage(absSender, chatId, "Что-то ты набаловался с вводными данными. Начни с самого начала", messageId, update);
        }
    }
}
