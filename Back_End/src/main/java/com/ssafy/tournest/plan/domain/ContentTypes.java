package com.ssafy.tournest.plan.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class ContentTypes {
    @Id @GeneratedValue
    @Column(name="contentTypeId")
    private Long Id;

    @Column(length = 45)
    private String contentTypeName;
}
