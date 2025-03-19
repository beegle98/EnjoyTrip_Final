package com.ssafy.tournest.plan.domain.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttractionDto {
    private Long no;
    private String title;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long likes;
    private String image;
}
