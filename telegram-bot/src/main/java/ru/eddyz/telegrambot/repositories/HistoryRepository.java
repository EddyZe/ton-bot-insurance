package ru.eddyz.telegrambot.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.telegrambot.domain.entities.History;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query(value = "select h from History h join User u on u.id = h.user.id where u.telegramChatId = :chatId")
    Page<History> findByTelegramChatId(@Param("chatId") Long chatId, Pageable pageable);

    List<History> findByHistoryStatus(HistoryStatus historyStatus);

}
