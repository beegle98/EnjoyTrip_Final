package com.ssafy.tournest.plan.domain.dto;

import lombok.*;
//import org.springframework.web.service.annotation.GetExchange;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlanDto {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic;
    private Long user_seq;
    private int location;
}
