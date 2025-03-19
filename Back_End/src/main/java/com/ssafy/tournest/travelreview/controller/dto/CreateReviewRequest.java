package com.ssafy.tournest.travelreview.controller.dto;

import com.ssafy.tournest.travelreview.domain.TravelReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReviewRequest {
    private Long travelReviewNo;
    private String subject;
    private String location;
    private Object content;
    private String userId;
    private int hit;
    private LocalDateTime registerTime;
    private LocalDateTime modifiedTime;
    // Notice 엔티티를 DTO로 변환하는 정적 메서드
    public static com.ssafy.tournest.travelreview.domain.dto.TravelReviewDto from(TravelReview travelReview, String username) {
        return com.ssafy.tournest.travelreview.domain.dto.TravelReviewDto.builder()
                .travelReviewNo(travelReview.getTravelReviewNo())
                .location(travelReview.getLocation())
                .subject(travelReview.getSubject())
                .content(travelReview.getContent())
                .userId(travelReview.getUserId())
                .username(username)
                .hit(travelReview.getHit())
                .registerTime(travelReview.getRegisterTime())
                .modifiedTime(travelReview.getModifiedTime())
                .thumbnailUrl(travelReview.getThumbnailUrl())
                .build();
    }
}
