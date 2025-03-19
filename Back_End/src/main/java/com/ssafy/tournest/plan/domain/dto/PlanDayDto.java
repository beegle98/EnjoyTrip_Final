package com.ssafy.tournest.plan.domain.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlanDayDto {
    private List<AttractionDto> places;
}
