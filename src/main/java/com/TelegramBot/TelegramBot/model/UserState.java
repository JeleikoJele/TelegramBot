package com.TelegramBot.TelegramBot.model;

import com.TelegramBot.TelegramBot.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_state")
public class UserState {
    @EmbeddedId
    private Id id;
    @Column(name = "user_degree")
    private double degree;
    @Column(name = "user_weight")
    private double weight;
    @Column(name = "user_percent")
    private double alcoholPercent;
    @Column(name = "user_time")
    private double time;
    @Enumerated
    @Column(name = "last_bot_state")
    private State nextState;
}
