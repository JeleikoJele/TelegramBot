package com.TelegramBot.TelegramBot.enums;

public enum State {
    DEGREE_STEP("Укажите степень опьянения:"),
    WEIGHT_STEP("Укажите ваш вес:"),
    PERCENT_STEP("Укажите процент алкоголя, который будете пить:"),
    TIME_STEP("Укажите время распития в часах:"),
    NULL_STEP("");

    private final String text;

    State(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
