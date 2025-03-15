package ru.eddyz.adminpanel.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpanel.domain.entities.Wallet;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {


    @Query(value = "select w from Wallet w join User u on w.user.id = u.id where u.telegramChatId = :telegramId")
    Optional<Wallet> findByUserTelegramId(@Param("telegramId") Long telegramId);

    Optional<Wallet> findByAccountId(String accountId);

}
