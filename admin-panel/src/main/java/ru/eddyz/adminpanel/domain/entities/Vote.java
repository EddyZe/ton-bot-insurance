package ru.eddyz.adminpanel.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import ru.eddyz.adminpanel.domain.enums.VotingSolution;

import java.time.LocalDateTime;

@Entity
@Table(name = "vote")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VotingSolution solution;

    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "history_id", referencedColumnName = "id")
    private History history;

}
