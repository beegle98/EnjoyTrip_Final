package com.ssafy.tournest.plan.domain;

import javax.persistence.*;

import com.ssafy.tournest.api.entity.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="plans")
@Setter
@Getter
@ToString
public class Plan {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SEQ")
    private User user; // 소유자 User와의 관계

    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PlanDay> planDays = new ArrayList<>();

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private boolean isPublic;

    private int location;
}
