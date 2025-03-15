package ru.eddyz.adminpanel.domain.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "history_file_path")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HistoryFilePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    @ManyToOne
    @JoinColumn(name = "history_file_id",  referencedColumnName = "id")
    private HistoryFile file;
}
