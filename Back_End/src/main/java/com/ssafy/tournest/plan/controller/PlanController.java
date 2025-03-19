package com.ssafy.tournest.plan.controller;

import com.ssafy.tournest.TournestApplication;
import com.ssafy.tournest.api.controller.user.UserController;
import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.api.service.UserService;
import com.ssafy.tournest.attractionboard.service.LikeService;
import com.ssafy.tournest.plan.domain.dto.*;
import com.ssafy.tournest.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/plan")
@RestController
@RequiredArgsConstructor
@Tag(name="Plan", description = "여행 계획 관리")
@Slf4j
public class PlanController {

    private final PlanService planService;
    private final LikeService likeService;

    @GetMapping("/attraction/list")
    @Operation(summary = "여행지 목록 조회", description = "각 여행지의 정보 및 좋아요 개수를 조회합니다.")
    public ResponseEntity<?> list(@ModelAttribute PageFilter pageFilter){


        log.info("Controller.......여행지 목록 조회");
        System.out.println("pageFilter" + pageFilter);
        Page<AttractionDto> planList = planService.getAttractions(pageFilter);

        return new ResponseEntity<>(planList, HttpStatus.OK);
    }

    @GetMapping("/search/attraction/list")
    @Operation(summary = "여행지 검색 목록 조회", description = "각 여행지의 정보 및 좋아요 사용자의 좋아요 여부를 조회합니다.")
    public ResponseEntity<?> searchAttractionlist(@ModelAttribute PageFilter pageFilter){

        System.out.println("pageFilter" + pageFilter);
        List<AttractionSearchDto> planList = planService.getSearchAttractions(pageFilter);
        return new ResponseEntity<>(planList, HttpStatus.OK);
    }

    @GetMapping("/my/attraction/list")
    @Operation(summary = "나의 여행지 목록 조회", description = "각 여행지의 정보 및 좋아요 사용자의 좋아요 여부를 조회합니다.")
    public ResponseEntity<?> searchMyAttractionlist(){
        List<AttractionSearchDto> planList = planService.getSearchMyAttractions();
        return new ResponseEntity<>(planList, HttpStatus.OK);
    }

    @PostMapping("/{attractionId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long attractionId) {
        System.out.println("attractionId....." + attractionId);
        boolean isLiked = likeService.toggleLike(attractionId);
        return ResponseEntity.ok(Map.of("liked", isLiked));
    }





    @PostMapping("/attraction/save")
    public ResponseEntity<Map<String, Object>> saveOrUpdatePlan(@RequestBody PlanRequestDto planRequestDto) {
        System.out.println(planRequestDto.getDaysData().toString());
        Long planId = planService.saveOrUpdatePlan(planRequestDto);

        // Fetch the updated plan and daysData
        PlanResponseDto planResponse = planService.getPlanById(planId);

        Map<String, Object> response = new HashMap<>();
        response.put("plan", planResponse.getPlan());
        response.put("daysData", planResponse.getDaysData());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/attraction/list/{planId}")
    public ResponseEntity<PlanResponseDto> getPlan(@PathVariable Long planId) {
        PlanResponseDto planResponse = planService.getPlanById(planId);
        System.out.println("planList" + planResponse.toString());
        return ResponseEntity.ok(planResponse);
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<?> getUserPlan(@PathVariable Long userId) {
        List<PlanDto> plan = planService.getPlanByUserId(userId);
        System.out.println("성공" + plan.toString());
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }

    @GetMapping("/list/region")
    public ResponseEntity<?> getRegionPlan(@ModelAttribute  PlanFilter planFilter) {
        Pageable pageable = PageRequest.of(planFilter.getPage(), planFilter.getSize());
        System.out.println(planFilter);
        Page<PlanDto> plan = planService.getPlanByRegion(planFilter.getRegionId(), pageable);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(@PathVariable Long planId) {
        try {
            planService.deletePlanById(planId);
            return ResponseEntity.ok("Plan deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete the plan: " + e.getMessage());
        }
    }


}
