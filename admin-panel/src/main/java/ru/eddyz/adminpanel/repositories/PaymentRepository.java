package ru.eddyz.adminpanel.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpanel.domain.entities.Payment;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {


    @Query(value = "select p from Payment p join User u on u.id = p.user.id where u.telegramChatId = :telegramId")
    Page<Payment> findByTelegramId(@Param("telegramId") Long telegramId, Pageable pageable);

}
