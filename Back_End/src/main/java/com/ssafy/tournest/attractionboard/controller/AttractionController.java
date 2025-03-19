package com.ssafy.tournest.attractionboard.controller;


import com.ssafy.tournest.attractionboard.service.AttractionService;
import com.ssafy.tournest.attractionboard.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;
    private final LikeService likeService;

    // 1. 여행지 리스트와 좋아요 정보 반환
//    @GetMapping
//    public ResponseEntity<?> getAttractions() {
//        List<Map<String, Object>> attractions = attractionService.getAttractionsWithLikes(user);
//        return ResponseEntity.ok(attractions);
//    }
//
//    // 2. 좋아요 상태 변경
//    @PostMapping("/{attractionId}/like")
//    public ResponseEntity<?> toggleLike(
//            @PathVariable Long attractionId) {
//
//        boolean isLiked = likeService.toggleLike(user, attractionId);
//        return ResponseEntity.ok(Map.of("liked", isLiked));
//    }
}