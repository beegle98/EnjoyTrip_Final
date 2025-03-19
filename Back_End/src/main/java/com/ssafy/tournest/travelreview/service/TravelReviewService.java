package com.ssafy.tournest.travelreview.service;

import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.api.service.UserService;
import com.ssafy.tournest.travelreview.domain.TravelReview;
import com.ssafy.tournest.travelreview.domain.dto.TravelReviewDto;
import com.ssafy.tournest.travelreview.repository.TravelReviewRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelReviewService {

    private final TravelReviewRepository travelReviewRepository;
    private final UserService userService; // 사용자 정보를 가져오기 위한 서비스

    @Value("${user.dir}")
    private String projectPath;

    // userID로 리뷰 목록 조회
    public List<TravelReviewDto> getTravelReviewsByUserId(String userId) {
        return travelReviewRepository.findByUserIdOrderByTravelReviewNoDesc(userId)
                .stream()
                .map(review -> {
                    User user = userService.getUser(review.getUserId());
                    return TravelReviewDto.from(review, user.getUsername(), user.getProfileImageUrl());
                })
                .collect(Collectors.toList());
    }
    // 여행지 리뷰 목록 조회
    @Transactional(readOnly = true)
    public Page<TravelReviewDto> getTravelReviewList(String key, String word, Pageable pageable) {
        Page<TravelReview> travelReviewPage = travelReviewRepository.findBySearchCondition(key, word, pageable);
        return travelReviewPage.map(travelReview -> {
            User user = userService.getUser(travelReview.getUserId());
            return TravelReviewDto.from(travelReview, user.getUsername(), user.getProfileImageUrl());
        });
    }

    // 여행지 리뷰 상세 조회
    public TravelReviewDto getTravelReview(Long travelReviewNo) {
        TravelReview travelReview = travelReviewRepository.findById(travelReviewNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행지 리뷰가 존재하지 않습니다."));
        travelReview.increaseHit();
        User user = userService.getUser(travelReview.getUserId());
        return TravelReviewDto.from(travelReview, user.getUsername(), user.getProfileImageUrl());
    }

    // 여행지 리뷰 등록
    public TravelReviewDto createTravelReview(TravelReview travelReview, MultipartFile profileImage) {
        String imageUrl = saveImage(profileImage);
        travelReview.setThumbnailUrl(imageUrl);

        TravelReview savedTravelReview = travelReviewRepository.save(travelReview);
        User user = userService.getUser(savedTravelReview.getUserId());
        return TravelReviewDto.from(savedTravelReview, user.getUsername(), user.getProfileImageUrl());
    }

    // 여행지 리뷰 수정
    public TravelReviewDto updateTravelReview(Long travelReviewNo, TravelReview travelReviewRequest, MultipartFile profileImage) {
        TravelReview travelReview = travelReviewRepository.findById(travelReviewNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행지 리뷰가 존재하지 않습니다."));

        String imageUrl = saveImage(profileImage);
        travelReview.setThumbnailUrl(imageUrl);
        System.out.println("imageUrl.......................: " + imageUrl);
        travelReview.update(travelReviewRequest.getSubject(), travelReviewRequest.getContent());
        User user = userService.getUser(travelReview.getUserId());
        return TravelReviewDto.from(travelReview, user.getUsername(), user.getProfileImageUrl());
    }

    // 여행지 리뷰 삭제
    public void deleteTravelReview(Long travelReviewNo) {
        TravelReview travelReview = travelReviewRepository.findById(travelReviewNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행지 리뷰가 존재하지 않습니다."));
        travelReviewRepository.delete(travelReview);
    }
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String saveImage(MultipartFile thumbnailImage){
        String imageUrl = null;

        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            try {
                // 파일 이름 생성 (중복 방지를 위해 UUID 사용)
                String fileName = UUID.randomUUID().toString() +
                        getFileExtension(thumbnailImage.getOriginalFilename());
                System.out.println("fileName = " + fileName);
                System.out.println("thumbnailImage = " + thumbnailImage.getOriginalFilename());

                // 프로젝트 루트 디렉토리 아래에 업로드 디렉토리 생성
                String uploadPath = projectPath + File.separator + "src" + File.separator +
                        "main" + File.separator + "resources" + File.separator +
                        "static" + File.separator + "images"+ File.separator + "thumbnailImages";
                File uploadDir = new File(uploadPath);
                System.out.println("uploadPath = " + uploadPath);

                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs();
                    System.out.println("Directory created: " + created);
                }

                // 파일 저장
                System.out.println("path: "+uploadDir + File.separator + fileName);
                File destination = new File(uploadDir + File.separator + fileName);
                thumbnailImage.transferTo(destination);
                // 이미지 URL 생성
                imageUrl = "/images/thumbnailImages/" + fileName;
                System.out.println("imageUrl = " + imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패", e);
            }
        }
        return imageUrl;
    }
}