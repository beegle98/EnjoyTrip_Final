package com.ssafy.tournest.travelreview.domain.dto;

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
public class TravelReviewDto {
    private Long travelReviewNo;
    private String subject;
    private String location;
    private Object content;
    private String userId;
    private String username;  // 추가된 사용자 이름 필드
    private String profileImageUrl;
    private int hit;
    private LocalDateTime registerTime;
    private LocalDateTime modifiedTime;
    private String thumbnailUrl;
    // Notice 엔티티를 DTO로 변환하는 정적 메서드
    public static TravelReviewDto from(TravelReview travelReview, String username, String profileImageUrl) {
        return TravelReviewDto.builder()
                .travelReviewNo(travelReview.getTravelReviewNo())
                .location(travelReview.getLocation())
                .subject(travelReview.getSubject())
                .content(travelReview.getContent())
                .userId(travelReview.getUserId())
                .username(username)
                .profileImageUrl(profileImageUrl)
                .hit(travelReview.getHit())
                .registerTime(travelReview.getRegisterTime())
                .modifiedTime(travelReview.getModifiedTime())
                .thumbnailUrl(travelReview.getThumbnailUrl())
                .build();
    }
}
