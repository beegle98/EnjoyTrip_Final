package com.ssafy.tournest.attractionboard.domain;

import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.plan.domain.Attraction;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Setter
@ToString
public class Like {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
}