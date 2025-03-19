package com.ssafy.tournest.attractionboard.service;


import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.attractionboard.repository.LikeRepository;
import com.ssafy.tournest.plan.domain.Attraction;
import com.ssafy.tournest.plan.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final LikeRepository likeRepository;

//    public List<Map<String, Object>> getAttractionsWithLikes(User user) {
//        List<Attraction> attractions = attractionRepository.findAll();
//
//
//
//        return attractions.stream()
//                .map(attraction -> {
//                    boolean isLiked = likeRepository.existsByUserAndAttraction(user, attraction);
//                    return Map.of(
//                            "id", attraction.getId(),
//                            "title", attraction.getTitle(),
//                            "image", attraction.getFirst_image1(),
//                            "overview", attraction.getOverview(),
//                            "isLiked", isLiked
//                    );
//                })
//                .collect(Collectors.toList());
//    }
}