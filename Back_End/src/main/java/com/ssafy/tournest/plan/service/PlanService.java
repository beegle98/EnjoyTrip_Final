package com.ssafy.tournest.plan.service;

import com.ssafy.tournest.plan.domain.Plan;
import com.ssafy.tournest.plan.domain.PlanDay;
import com.ssafy.tournest.plan.domain.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PlanService {
    Page<AttractionDto> getAttractions(PageFilter filter);
    Long saveOrUpdatePlan(PlanRequestDto requestDto);
    PlanResponseDto getPlanById(Long planId);
    List<PlanDto> getPlanByUserId(Long userId);
    Page<PlanDto> getPlanByRegion(int regionId, Pageable pageable);
    void deletePlanById(Long planId);
    List<AttractionSearchDto> getSearchAttractions(PageFilter filter);
    List<AttractionSearchDto> getSearchMyAttractions();
}
