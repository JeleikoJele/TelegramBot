package com.TelegramBot.TelegramBot.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ConfigBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.key}")
    private String botKey;

    @Value("${bot.photos.path}")
    private String memesPath;

    @Value("${bot.HLEP.path}")
    private String hlepPath;

    @Value("${bot.audio.path}")
    private String audioPath;

}
