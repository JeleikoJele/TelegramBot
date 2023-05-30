package com.TelegramBot.TelegramBot.service.user_state_handler;

import com.TelegramBot.TelegramBot.config.ConfigBot;
import com.TelegramBot.TelegramBot.enums.State;
import com.TelegramBot.TelegramBot.model.UserState;
import com.TelegramBot.TelegramBot.repository.UserStateRepository;
import com.TelegramBot.TelegramBot.service.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Order(value = 0)
public class DegreeHandler extends MessageUtils implements UserStateHandler {

    @Override
    public void handleUpdate(AbsSender absSender, Update update, UserState userState,
                             long chatId, UserStateRepository userStateRepository, int messageId, ConfigBot configBot) {

        SendMessage sendMessage = SendMessage.builder()
                .text(State.DEGREE_STEP.toString())
                .chatId(chatId)
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsLine = new ArrayList<>();
        List<InlineKeyboardButton> oneRowInLine = new ArrayList<>();

        oneRowInLine.add(createButton("Легкая", "LIGHT_BUTTON"));
        oneRowInLine.add(createButton("Средняя", "AVERAGE_BUTTON"));
        oneRowInLine.add(createButton("В зюзю", "SEVERE_BUTTON"));

        rowsLine.add(oneRowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsLine);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private InlineKeyboardButton createButton(String text, String callBackData) {

        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callBackData)
                .build();
    }
}
