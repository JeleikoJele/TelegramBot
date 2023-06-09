package com.TelegramBot.TelegramBot.service;

import com.TelegramBot.TelegramBot.config.ConfigBot;
import com.TelegramBot.TelegramBot.enums.State;
import com.TelegramBot.TelegramBot.model.UserState;
import com.TelegramBot.TelegramBot.model.Id;
import com.TelegramBot.TelegramBot.repository.UserStateRepository;
import com.TelegramBot.TelegramBot.service.user_state_handler.UserStateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static com.TelegramBot.TelegramBot.enums.State.WEIGHT_STEP;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    private final ConfigBot configBot;
    private final UserStateRepository userStateRepository;
    private final List<UserStateHandler> handlers;
    MessageUtils messageUtils = new MessageUtils();

    @Override
    public String getBotUsername() {
        return configBot.getBotName();
    }

    @Override
    public String getBotToken() {
        return configBot.getBotKey();
    }

    @PostConstruct
    public void buttonSetUp() {
        try {
            execute(new SetMyCommands(List.of(
                    new BotCommand("/start", "Это только начало"),
                    new BotCommand("/help", "ПАМАГИТИ"),
                    new BotCommand("/send_meme", "Рофлани, ковбой"),
                    new BotCommand("/calculate", "Алкогольная математика. Спиртовая алкоритмика.")),
                    new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Bot commands error occurred" + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {

        long chatId;
        long userId;
        int messageId;
        String userFirstName;
        String receivedMessage;

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            userFirstName = update.getMessage().getFrom().getFirstName();
            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
            messageId = update.getMessage().getMessageId();

            UserState userState = userStateRepository.findByIdChatIdAndIdUserId(chatId, userId);
            if (userState == null) {
                userState = UserState.builder()
                        .id(new Id(userId, chatId))
                        .build();
                userStateRepository.save(userState);
            }

            botCommands(messageText, userFirstName, chatId, update, userState, messageId, userId);

        } else if (update.hasCallbackQuery()) {

            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            receivedMessage = update.getCallbackQuery().getData();
            messageId = update.getCallbackQuery().getMessage().getMessageId();

            UserState userState = userStateRepository.findByIdChatIdAndIdUserId(chatId, userId);

            chooseDegree(receivedMessage, userState);

            if (userState.getDegree() != 0) {
                userState.setNextState(WEIGHT_STEP);
                userStateRepository.save(userState);
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text(WEIGHT_STEP.toString())
                        .build();
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private void rollbackUserState(UserState userState) {
        UserState rolledBack = UserState.builder()
                .id(new Id(userState.getId().getUserId(),
                        userState.getId().getChatId()))
                .build();

        userStateRepository.save(rolledBack);
    }

    public void chooseDegree(String degreeState, UserState userState) {

        switch (degreeState) {

            case "LIGHT_BUTTON" -> {
                double degree = 1.5;
                userState.setDegree(degree);
            }
            case "AVERAGE_BUTTON" -> {
                double degree = 2;
                userState.setDegree(degree);
            }
            case "SEVERE_BUTTON" -> {
                double degree = 3;
                userState.setDegree(degree);
            }
        }
    }

    public void botCommands(String messageText, String userFirstName, long chatId,
                            @NotNull Update update, UserState userState, int messageId, long userId) {

        switch (messageText) {
            case "/start", "/start@ZeleleleleBobaBot" -> startCommand(chatId, userFirstName, update, messageId);

            case "/help", "/help@ZeleleleleBobaBot" -> helpCommand(chatId, update, messageId);

            case "/send_meme", "/send_meme@ZeleleleleBobaBot" ->
                    messageUtils.sendPhoto(this, configBot, chatId, messageId, update);
            case "/calculate", "/calculate@ZeleleleleBobaBot" -> {
                rollbackUserState(userState);

                if (userState.getId().getUserId() == userId && userState.getId().getChatId() == chatId) {
                    handlers.get(0).handleUpdate(this, update, userState, chatId, userStateRepository, messageId, configBot);
                }
            }

            default -> checkState(chatId, update, userState, messageId, userId);
        }
    }

    public void checkState(long chatId, Update update, UserState userState,
                           int messageId, long userId) {

        if (userState.getId().getUserId() == userId && userState.getId().getChatId() == chatId) {

            switch (Optional.ofNullable(userState.getNextState()).orElse(State.NULL_STEP)) {
                case WEIGHT_STEP -> handlers.get(1).handleUpdate(this, update, userState,
                        chatId, userStateRepository, messageId, configBot);

                case PERCENT_STEP -> handlers.get(2).handleUpdate(this, update, userState,
                        chatId, userStateRepository, messageId, configBot);

                case TIME_STEP -> handlers.get(3).handleUpdate(this, update, userState,
                        chatId, userStateRepository, messageId, configBot);
            }
        }
    }

    public void startCommand(long chatId, String userFirstName, Update update, int messageId) {

        String text = "Здравствуй, " + userFirstName + "! Если сегодня появилось желание выпить, то данный калькулятор как раз для тебя. " +
                "Так же можешь воспользоваться мемным хранилищем и немного преисполниться в своем познании. " +
                "Для ознакомления с возможностями данного бота воспользуйтесь коммандой /help. " +
                "И не забывай, что чрезмерное употребление акоголя вредит твоему здоровью. Так что лучше пойди и подучи джаву.";

        messageUtils.sendMessage(this, chatId, text, messageId, update);

    }

    public void helpCommand(long chatId, Update update, int messageId) {
        messageUtils.sendHLEP(this, configBot, chatId, messageId, update);
        messageUtils.sendMessage(this, chatId, "Ви скозали ХЛЕП?", messageId, update);
    }
}




