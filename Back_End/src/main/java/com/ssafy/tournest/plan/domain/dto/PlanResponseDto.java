package com.ssafy.tournest.plan.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString
public class PlanResponseDto {
    private PlanDto plan;
    private List<PlanDayDto> daysData;
}
