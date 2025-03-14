package ru.eddyz.adminpanel.domain.entities;


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

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Wallet wallet;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Insurance> insurance;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Vote> votes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch =  FetchType.EAGER)
    private List<Payment> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<History> histories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<HistoryFile> files;

    @OneToMany(mappedBy = "user", fetch =  FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Withdraw> withdraws;
}
