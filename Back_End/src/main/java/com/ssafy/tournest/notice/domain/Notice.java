package com.ssafy.tournest.notice.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeDef(name = "json", typeClass = JsonType.class)

public class Notice {

    /** 게시글 번호 (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeNo;

    /** 제목 (최대 100자) */
    @Column(nullable = false, length = 100)
    private String subject;

    /** QuillEditor로 작성된 내용 (TEXT 타입) */
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Object content;    // Delta 객체를 JSON 형태로 저장

    /** 작성자 ID */
    @Column(nullable = false)
    private String userId;

    /** 조회수 */
    @Column(columnDefinition = "integer default 0")
    private int hit;

    /** 등록 시간 */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime registerTime;

    /** 수정 시간 */
    @Column
    private LocalDateTime modifiedTime;

    /** 게시글 수정 기능 */
    public void update(String subject, Object content) {
        this.subject = subject;
        this.content = content;
        this.modifiedTime = LocalDateTime.now();
    }

    /** 조회수 증가 기능 */
    public void increaseHit() {
        this.hit++;
    }
}