package com.ssafy.tournest.plan.repository;

import com.ssafy.tournest.plan.domain.PlanDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanDayRepository extends JpaRepository<PlanDay, Long> {

    @Query("SELECT pd FROM PlanDay pd WHERE pd.plan.id = :planId")
    List<PlanDay> findByPlanId(@Param("planId") Long planId);

    void deleteByPlanId(Long planId);

}
