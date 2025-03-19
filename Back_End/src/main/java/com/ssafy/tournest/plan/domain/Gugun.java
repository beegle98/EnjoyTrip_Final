package com.ssafy.tournest.plan.domain;

import javax.persistence.Entity;
import javax.persistence.*;


public class Gugun {
    @Id @GeneratedValue @Column(name="no")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sido_code", referencedColumnName = "sidoCode", nullable = false)
    private Sido sido; // Sido와 단방향 ManyToOne 관계

    @Column(unique = true, nullable = false)
    private Integer gugunCode;

    @Column(length = 20)
    private String gugunName;

}
