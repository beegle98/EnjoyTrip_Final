package com.ssafy.tournest.plan.repository;

import com.ssafy.tournest.plan.domain.PlanAttraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanAttractionRepository extends JpaRepository<PlanAttraction, Integer> {

    List<PlanAttraction> findByPlanDayId(Long planDayId);
}
