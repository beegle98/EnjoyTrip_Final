package com.ssafy.tournest.travelreview.controller;

import com.ssafy.tournest.api.service.dto.UserSignUpRequest;
import com.ssafy.tournest.travelreview.domain.TravelReview;
import com.ssafy.tournest.travelreview.domain.dto.TravelReviewDto;
import com.ssafy.tournest.travelreview.service.TravelReviewService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/travelReview")
public class TravelReviewController {

    private final TravelReviewService travelReviewService;

    public TravelReviewController(TravelReviewService travelReviewService) {
        this.travelReviewService = travelReviewService;
    }

    // userId로 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TravelReviewDto>> getTravelReviewsByUserId(@PathVariable String userId) {
        List<TravelReviewDto> reviews = travelReviewService.getTravelReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
    // 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTravelReviewList(
            @RequestParam(defaultValue = "1") int pgno,
            @RequestParam(defaultValue = "10") int spp,
            @RequestParam(defaultValue = "") String key,
            @RequestParam(defaultValue = "") String word) {

        PageRequest pageRequest = PageRequest.of(pgno - 1, spp, Sort.by("travelReviewNo").descending());
        Page<TravelReviewDto> travelReviewPage = travelReviewService.getTravelReviewList(key, word, pageRequest);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> body = new HashMap<>();

        body.put("travelReviews", travelReviewPage.getContent());
        body.put("currentPage", pgno);
        body.put("totalPageCount", travelReviewPage.getTotalPages());

        response.put("body", body);
        System.out.println("body"+body);

        return ResponseEntity.ok(response);
    }

    // 상세 조회
    @GetMapping("/{travelReviewNo}")
    public ResponseEntity<TravelReviewDto> getTravelReview(@PathVariable Long travelReviewNo) {
        return ResponseEntity.ok(travelReviewService.getTravelReview(travelReviewNo));
    }

    // 등록
    @PostMapping
    public ResponseEntity<TravelReviewDto> createTravelReview(
            @ModelAttribute TravelReview travelReview,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage
    ) {
        System.out.println("travelReview: " + travelReview.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(travelReviewService.createTravelReview(travelReview,thumbnailImage));
    }

    // 수정을 위한 조회
    @GetMapping("/modify/{travelReviewNo}")
    public ResponseEntity<TravelReviewDto> getModifyTravelReview(@PathVariable Long travelReviewNo) {
        return ResponseEntity.ok(travelReviewService.getTravelReview(travelReviewNo));
    }

    // 수정
    @PutMapping
    public ResponseEntity<TravelReviewDto> updateTravelReview(
            @ModelAttribute TravelReview travelReview,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage
    ) {
        System.out.println("ModifytravelReview: " + travelReview.toString());
        return ResponseEntity.ok(travelReviewService.updateTravelReview(travelReview.getTravelReviewNo(), travelReview, thumbnailImage));
    }

    // 삭제
    @DeleteMapping("/{travelReviewNo}")
    public ResponseEntity<Void> deleteTravelReview(@PathVariable Long travelReviewNo) {
        travelReviewService.deleteTravelReview(travelReviewNo);
        return ResponseEntity.ok().build();
    }
}
