package ru.eddyz.adminpanel.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import ru.eddyz.adminpanel.domain.enums.WithdrawStatus;

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

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;
}
