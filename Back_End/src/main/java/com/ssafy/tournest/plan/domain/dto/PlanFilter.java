package com.ssafy.tournest.plan.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlanFilter {
    private int regionId;
    private int page;
    private int size;
}
