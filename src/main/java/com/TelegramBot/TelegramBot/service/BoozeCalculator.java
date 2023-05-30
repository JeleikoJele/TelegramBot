package com.TelegramBot.TelegramBot.service;

import com.TelegramBot.TelegramBot.model.UserState;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@Data
@RequiredArgsConstructor
public class BoozeCalculator {

    public double calculateBaseAmount(UserState userState) {
        double result = 0;

        result = (userState.getDegree() * userState.getWeight() * 0.7) / (10 * userState.getAlcoholPercent() * 0.789);

        return result;
    }

    public double calculatePerHourAmount(UserState userState) {
            double result = 0;

            result = (2 / (userState.getAlcoholPercent() * 0.789)) * (userState.getTime() - 1);

            return result;
    }
}
