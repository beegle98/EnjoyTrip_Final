package com.ssafy.tournest.plan.domain;

import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan_days")
@Setter
@Getter
@ToString
public class PlanDay {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan; // 여행 계획과의 관계

    @Column(nullable = false)
    private Integer day; // 여행의 몇 번째 날인지

    @OneToMany(mappedBy = "planDay", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PlanAttraction> planAttractions = new ArrayList<>(); // PlanAttraction 연관 관계
}
