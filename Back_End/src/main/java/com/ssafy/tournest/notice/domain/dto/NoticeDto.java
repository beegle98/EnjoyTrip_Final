package com.ssafy.tournest.notice.domain.dto;

import com.ssafy.tournest.notice.domain.Notice;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDto {
    private Long noticeNo;
    private String subject;
    private Object content;
    private String userId;
    private String username;  // 추가된 사용자 이름 필드
    private int hit;
    private LocalDateTime registerTime;
    private LocalDateTime modifiedTime;
    // Notice 엔티티를 DTO로 변환하는 정적 메서드
    public static NoticeDto from(Notice notice, String username) {
        return NoticeDto.builder()
                .noticeNo(notice.getNoticeNo())
                .subject(notice.getSubject())
                .content(notice.getContent())
                .userId(notice.getUserId())
                .username(username)
                .hit(notice.getHit())
                .registerTime(notice.getRegisterTime())
                .modifiedTime(notice.getModifiedTime())
                .build();
    }
}

