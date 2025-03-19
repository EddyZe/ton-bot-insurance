package ru.eddyz.adminpanel.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpanel.domain.entities.Withdraw;
import ru.eddyz.adminpanel.domain.enums.WithdrawStatus;

import java.util.List;

@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {



    @Query(value = "select w from Withdraw w join User u on w.user.id = u.id where u.telegramChatId = :chatId")
    Page<Withdraw> findByTelegramChatId(@Param("chatId") Long chatId, Pageable pageable);

    List<Withdraw> findByStatus(WithdrawStatus status, Sort sort);
}
