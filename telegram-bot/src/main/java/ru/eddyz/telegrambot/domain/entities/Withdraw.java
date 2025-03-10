package ru.eddyz.telegrambot.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cglib.core.Local;
import ru.eddyz.telegrambot.domain.enums.WithdrawStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdraw")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Withdraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private Boolean active;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WithdrawStatus status;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;
}
