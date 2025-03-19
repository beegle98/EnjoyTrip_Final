package com.ssafy.tournest.plan.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttractionSearchDto {
    private Long id;
    private String name;
    private String title;
    private String image;
    private String link;
    private boolean isLiked;
}
