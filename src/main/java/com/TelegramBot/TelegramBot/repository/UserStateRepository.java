package com.TelegramBot.TelegramBot.repository;

import com.TelegramBot.TelegramBot.model.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserStateRepository extends JpaRepository<UserState, Integer> {
    UserState findByUserId(long userId);

}
