package com.ssafy.tournest.plan.domain;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="plan_attractions")
@Setter
@Getter
@ToString
public class PlanAttraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id")
    private Attraction attraction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_day_id")
    private PlanDay planDay;

    @Column(nullable = false)
    private Integer orderInDay;
}
