package ru.eddyz.telegrambot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

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

    private Double amount;
    private String token;
    private Boolean active;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;
}
