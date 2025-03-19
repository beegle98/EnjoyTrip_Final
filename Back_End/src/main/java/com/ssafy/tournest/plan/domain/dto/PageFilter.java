package com.ssafy.tournest.plan.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageFilter {
    private String article;
    private int areaCode;
    private int contentType;
    private int page; // 페이지 번호
    private int size; // 페이지 크기
}
