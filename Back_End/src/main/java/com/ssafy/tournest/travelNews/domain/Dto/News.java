package com.ssafy.tournest.travelNews.domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500, nullable = false)
    private String link; // 기사 링크 추가
}
