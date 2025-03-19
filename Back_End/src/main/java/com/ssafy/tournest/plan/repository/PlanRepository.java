package com.ssafy.tournest.plan.repository;

import com.ssafy.tournest.plan.domain.Plan;
import com.ssafy.tournest.plan.domain.dto.PlanDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByUser_UserSeq(Long userSeq);


    @Query("SELECT new com.ssafy.tournest.plan.domain.dto.PlanDto(" +
            "p.id, p.title, p.startDate, p.endDate, p.isPublic, p.user.userSeq, p.location) " +
            "FROM Plan p WHERE (:regionId = 0 or p.location = :regionId) and p.isPublic = true")
    Page<PlanDto> findByRegion(@Param("regionId") int regionId, Pageable pageable);

    void deleteById(Long planId);
}
