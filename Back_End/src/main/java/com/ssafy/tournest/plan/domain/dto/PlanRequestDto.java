package com.ssafy.tournest.plan.domain.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PlanRequestDto {
    private PlanDto plan;
    private List<PlanDayDto> daysData;
}
