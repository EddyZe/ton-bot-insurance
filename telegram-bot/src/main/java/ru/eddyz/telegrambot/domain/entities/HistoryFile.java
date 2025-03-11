package ru.eddyz.telegrambot.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import ru.eddyz.telegrambot.domain.enums.HistoryFileType;

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

    private String telegramFileGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryFileType fileType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "history_id", referencedColumnName = "id")
    private History history;
}
