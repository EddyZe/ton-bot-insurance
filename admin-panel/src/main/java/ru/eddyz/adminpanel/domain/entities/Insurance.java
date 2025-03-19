package ru.eddyz.adminpanel.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "insurance")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private LocalDateTime endDate;
    @Column(nullable = false)
    private Boolean active;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
