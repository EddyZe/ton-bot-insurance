package ru.eddyz.telegrambot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usr")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false)
    private Long telegramChatId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean active;

    @OneToOne(mappedBy = "user")
    private Wallet wallet;

    @OneToMany(mappedBy = "user")
    private List<Insurance> insurance;

    @OneToMany(mappedBy = "user")
    private List<Vote> votes;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    @OneToMany(mappedBy = "user")
    private List<History> histories;

    @OneToMany(mappedBy = "user")
    private List<HistoryFile> files;

}
