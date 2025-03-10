package ru.eddyz.telegrambot.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.telegrambot.domain.entities.Insurance;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {


    @Query(value = "select i from Insurance i join User u on u.id = i.user.id where u.telegramChatId = :chatId and i.active = true")
    Optional<Insurance> findByChatIdIsActive(@Param("chatId") Long chatId);

    @Query(value = "select i from Insurance i join User u on u.id = i.user.id where u.telegramChatId = :chatId")
    Page<Insurance> findByChatId(@Param("chatId") Long chatId, Pageable pageable);

    List<Insurance> findByActive(Boolean active);
}
