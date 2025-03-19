package com.ssafy.tournest.attractionboard.service;

import com.ssafy.tournest.api.controller.user.UserController;
import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.api.service.UserService;
import com.ssafy.tournest.attractionboard.domain.Like;
import com.ssafy.tournest.attractionboard.repository.LikeRepository;
import com.ssafy.tournest.plan.domain.Attraction;
import com.ssafy.tournest.plan.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final AttractionRepository attractionRepository;
    private final UserService userService;

    @Transactional
    public boolean toggleLike(Long attractionId) {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userService.getUser(principal.getUsername());
        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Attraction ID"));
        Like like = likeRepository.findByUserAndAttraction(user, attraction).orElse(null);

        if (like != null) {
            likeRepository.delete(like);
            return false; // 좋아요 취소 상태
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setAttraction(attraction);
            likeRepository.save(newLike);
            return true; // 좋아요 상태
        }
    }
}