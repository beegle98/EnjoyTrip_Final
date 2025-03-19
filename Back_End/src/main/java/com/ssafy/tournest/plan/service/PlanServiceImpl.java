package com.ssafy.tournest.plan.service;

import com.ssafy.tournest.TournestApplication;
import com.ssafy.tournest.api.controller.user.UserController;
import com.ssafy.tournest.attractionboard.domain.Like;
import com.ssafy.tournest.attractionboard.repository.AttractionNewsRepository;
import com.ssafy.tournest.attractionboard.repository.LikeRepository;
import com.ssafy.tournest.plan.domain.*;
import com.ssafy.tournest.plan.domain.dto.*;
import com.ssafy.tournest.plan.repository.*;
import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService{

    private final PlanRepository planRepository;
    private final PlanDayRepository planDayRepository;
    private final PlanAttractionRepository planAttractionRepository;
    private final AttractionRepository attractionRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final AttractionNewsRepository attractionNewsRepository;


    // Save or Update Plan
    @Override
    public Long saveOrUpdatePlan(PlanRequestDto requestDto) {
        Plan plan;
        Long userId = UserController.userSeq;
        // Check if update or create
        if (requestDto.getPlan().getId() != null) {
            plan = planRepository.findById(requestDto.getPlan().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
            plan.setTitle(requestDto.getPlan().getTitle());
            plan.setStartDate(requestDto.getPlan().getStartDate());
            plan.setEndDate(requestDto.getPlan().getEndDate());
            plan.setPublic(requestDto.getPlan().isPublic());
            plan.setLocation(requestDto.getPlan().getLocation());
        } else {
            plan = new Plan();
            plan.setTitle(requestDto.getPlan().getTitle());
            plan.setStartDate(requestDto.getPlan().getStartDate());
            plan.setEndDate(requestDto.getPlan().getEndDate());
            plan.setPublic(requestDto.getPlan().isPublic());
            plan.setLocation(requestDto.getPlan().getLocation());
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Cannot found User"));
            plan.setUser(user);
        }

        plan = planRepository.save(plan);

        log.info(plan.toString());
        // Save Plan Days and Attractions
        if(requestDto.getPlan().getId() != null){
            planDayRepository.deleteByPlanId(plan.getId()); // Delete old days if updating
        }
        savePlanDays(plan, requestDto.getDaysData());

        return plan.getId();
    }

    // Save Plan Days
    private void savePlanDays(Plan plan, List<PlanDayDto> daysData) {

        for(int i = 0; i < daysData.size();i++){
            PlanDayDto dayDto = daysData.get(i);
            PlanDay planDay = new PlanDay();
            planDay.setPlan(plan);
            planDay.setDay(i + 1);
            PlanDay savedDay = planDayRepository.save(planDay);
            System.out.println("planday " + savedDay.toString());
            // Save Plan Attractions
            savePlanAttractions(savedDay, dayDto.getPlaces());

        }
    }

    // Save Plan Attractions
    private void savePlanAttractions(PlanDay planDay, List<AttractionDto> places) {
        int order = 1;
        for (AttractionDto attractionDto : places) {
            PlanAttraction planAttraction = new PlanAttraction();
            planAttraction.setPlanDay(planDay);
            Attraction attraction = attractionRepository.findById(attractionDto.getNo())
                    .orElseThrow(() -> new IllegalArgumentException("Attraction not found with id: " + attractionDto.getNo()));
            planAttraction.setAttraction(attraction);
            planAttraction.setOrderInDay(order++);
            planAttractionRepository.save(planAttraction);
        }
    }

    // Get Plan by ID
    @Override
    public PlanResponseDto getPlanById(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        return toResponseDto(plan);
    }

    @Override
    public List<PlanDto> getPlanByUserId(Long userId) {
        List<Plan> planList = planRepository.findByUser_UserSeq(userId);
        List<PlanDto> planDto = new ArrayList<>();
        for(Plan plan:planList){
            PlanDto pl = new PlanDto(plan.getId(), plan.getTitle(), plan.getStartDate(), plan.getEndDate(), plan.isPublic(),
                    plan.getUser().getUserSeq(), plan.getLocation());
            planDto.add(pl);
        }

        return planDto;
    }

    @Override
    public Page<PlanDto> getPlanByRegion(int regionId, Pageable pageable) {
        return planRepository.findByRegion(regionId, pageable);
    }

    @Override
    public void deletePlanById(Long planId) {
        planRepository.deleteById(planId);
    }

    @Override
    public List<AttractionSearchDto> getSearchAttractions(PageFilter filter) {

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Long userId = UserController.userSeq;
        Optional<User> user = userRepository.findById(userId);
        Page<AttractionDto> attractionDtos = attractionRepository.findAttractionsByFilter(
                filter.getAreaCode(),
                filter.getArticle(),
                filter.getContentType(),
                pageable);

        List<AttractionSearchDto> attractionSearchDto = new ArrayList<>();
        for (AttractionDto dto : attractionDtos.getContent()) {
            AttractionNews attrNews = attractionNewsRepository.findByAttrName(dto.getTitle());
            AttractionSearchDto asd = new AttractionSearchDto();
            asd.setId(dto.getNo());
            asd.setName(dto.getTitle());
            asd.setImage(dto.getImage());
            Optional<Like> like = likeRepository.findLikeByUserIdAndAttractionId(userId, dto.getNo());
            if(like.isEmpty()){
                asd.setLiked(false);
            }else{
                asd.setLiked(true);
            }
            if(attrNews != null){
                asd.setLink(attrNews.getLink());
                asd.setTitle(asd.getTitle());
            }

            attractionSearchDto.add(asd);
        }

        return attractionSearchDto;
    }

    @Override
    public List<AttractionSearchDto> getSearchMyAttractions() {
        Long userId = UserController.userSeq;
        List<Attraction> likedAttractions = likeRepository.findLikedAttractionsByUserId(userId);

        List<AttractionSearchDto> attractionSearchDto = new ArrayList<>();
        for (Attraction dto : likedAttractions) {
            AttractionNews attrNews = attractionNewsRepository.findByAttrName(dto.getTitle());
            AttractionSearchDto asd = new AttractionSearchDto();
            asd.setId(dto.getId());
            asd.setName(dto.getTitle());
            asd.setImage(dto.getFirst_image1());
            asd.setLiked(true);
            if(attrNews != null){
                asd.setLink(attrNews.getLink());
                asd.setTitle(asd.getTitle());
            }

            attractionSearchDto.add(asd);
        }

        return attractionSearchDto;
    }


    private PlanResponseDto toResponseDto(Plan plan) {
        PlanResponseDto responseDto = new PlanResponseDto();
        responseDto.setPlan(toPlanDto(plan));
        List<PlanDay> planDays = planDayRepository.findByPlanId(plan.getId());

        List<PlanDayDto> planList = new ArrayList<>();
        for(PlanDay pd : planDays){
            List<PlanAttraction> planAttractions = planAttractionRepository.findByPlanDayId(pd.getId());
            List<AttractionDto> attractiondto = new ArrayList<>();
            for(PlanAttraction planAttraction : planAttractions){
                Attraction attraction = attractionRepository.findById(planAttraction.getAttraction().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Attraction not found"));
                AttractionDto attractionDto = new AttractionDto(attraction.getId(), attraction.getTitle(),
                        attraction.getAddr1(), attraction.getLatitude(), attraction.getLongitude(),
                        likeRepository.countLikesByAttractionIdWithGroupBy(attraction.getId()), attraction.getFirst_image1());

                attractiondto.add(attractionDto);
            }
            PlanDayDto planDayDto = new PlanDayDto(attractiondto);
            planList.add(planDayDto);
        }
        responseDto.setDaysData(planList);

        return responseDto;
    }

    private PlanDto toPlanDto(Plan plan) {
        PlanDto dto = new PlanDto();
        dto.setId(plan.getId());
        dto.setTitle(plan.getTitle());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setPublic(plan.isPublic());
        dto.setUser_seq(plan.getUser().getUserSeq());
        return dto;
    }

    @Override
    public Page<AttractionDto> getAttractions(PageFilter filter) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        return attractionRepository.findAttractionsByFilter(
                filter.getAreaCode(),
                filter.getArticle(),
                filter.getContentType(),
                pageable
        );
    }
}

