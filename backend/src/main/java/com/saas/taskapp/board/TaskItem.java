package com.saas.taskapp.board;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TaskItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status; // TODO: replace with enum (TODO, IN_PROGRESS, DONE)
    private Integer position;

    @ManyToOne(optional = false)
    private Board board;
}

