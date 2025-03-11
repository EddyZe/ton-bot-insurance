package ru.eddyz.telegrambot.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4096, nullable = false)
    private String description;

    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private Boolean approve;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryStatus historyStatus;

    @OneToMany(mappedBy = "history", cascade =  CascadeType.REMOVE)
    private List<Vote> votes;

    @OneToMany(mappedBy = "history", cascade = CascadeType.REMOVE)
    private List<HistoryFile> files;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
