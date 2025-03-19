package com.ssafy.tournest.plan.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


public class Sido {
    @Id
    @GeneratedValue
    private Long no;

    @Column(unique = true, nullable = false)
    private Integer sidoCode;

    @Column(length = 20)
    private String sidoName;
}
