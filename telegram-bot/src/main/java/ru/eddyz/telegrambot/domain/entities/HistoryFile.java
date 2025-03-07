package ru.eddyz.telegrambot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_file")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HistoryFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String telegramFileId;

    @Column(nullable = false)
    private String telegramFileGroup;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "history_file_id", referencedColumnName = "id")
    private History history;
}
