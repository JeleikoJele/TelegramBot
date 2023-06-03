package com.TelegramBot.TelegramBot.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Id implements Serializable {
    @Column(name = "user_id")
    private long userId;
    @Column(name = "chat_id")
    private long chatId;
}
