package ru.eddyz.adminpanel.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import ru.eddyz.adminpanel.domain.enums.HistoryFileType;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HistoryFilePath> filePaths;
}
