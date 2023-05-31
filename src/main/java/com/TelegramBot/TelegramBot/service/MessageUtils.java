package com.TelegramBot.TelegramBot.service;

import com.TelegramBot.TelegramBot.config.ConfigBot;
import com.TelegramBot.TelegramBot.enums.State;
import com.TelegramBot.TelegramBot.model.UserState;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class MessageUtils {

    public void warnUser(AbsSender absSender, State state, UserState userState, long chatId) {

        userState.setNextState(state);
        String text = "Введите только число!";
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessage(AbsSender absSender, long chatId, String text, int messageId, Update update) {

        if (update.getMessage().getChat().isUserChat()) {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            try {
                absSender.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }

        } else {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .replyToMessageId(messageId)
                    .text(text)
                    .build();
            try {
                absSender.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void sendAudio(AbsSender absSender, ConfigBot configBot, long chatId,
                          int messageId, Update update) {

        if (update.getMessage().getChat().isUserChat()) {

            SendAudio sendAudio = SendAudio.builder()
                    .audio(new InputFile(new File(configBot.getAudioPath())))
                    .chatId(chatId)
                    .build();
            try {
                absSender.execute(sendAudio);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }

        } else {

            SendAudio sendAudio = SendAudio.builder()
                    .audio(new InputFile(new File(configBot.getAudioPath())))
                    .chatId(chatId)
                    .replyToMessageId(messageId)
                    .build();
            try {
                absSender.execute(sendAudio);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }

        }
    }

    public void sendHLEP(AbsSender absSender, ConfigBot configBot, long chatId,
                         int messageId, Update update){
        if (update.getMessage().getChat().isUserChat()) {

            try{
            SendPhoto sendPhoto = SendPhoto.builder()
                    .photo(new InputFile(new File(configBot.getHlepPath())))
                    .chatId(chatId)
                    .build();


                absSender.execute(sendPhoto);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        } else {

            try{
            SendPhoto sendPhotoWithReply = SendPhoto.builder()
                    .photo(new InputFile(new File(configBot.getHlepPath())))
                    .replyToMessageId(messageId)
                    .chatId(chatId)
                    .build();

                absSender.execute(sendPhotoWithReply);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }

    }

    public void sendPhoto(AbsSender absSender, ConfigBot configBot, long chatId,
                          int messageId, Update update) {

        if (update.getMessage().getChat().isUserChat()) {

            try {
                SendPhoto sendPhoto = SendPhoto.builder()
                        .photo(getMeme(configBot))
                        .chatId(chatId)
                        .build();

                absSender.execute(sendPhoto);
            } catch (TelegramApiException | FileNotFoundException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            try {
                SendPhoto sendPhotoWithReply = SendPhoto.builder()
                        .photo(getMeme(configBot))
                        .replyToMessageId(messageId)
                        .chatId(chatId)
                        .build();

                absSender.execute(sendPhotoWithReply);
            } catch (TelegramApiException | FileNotFoundException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private InputFile getMeme(ConfigBot configBot) throws IOException {

        List<Path> memes = Files.walk(Paths.get(configBot.getMemesPath()))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        Collections.shuffle(memes);

        Random random = new Random();
        int randomNumber = random.nextInt(memes.size() - 1);
        return new InputFile(new File(memes.get(randomNumber).toString()));
    }
}
