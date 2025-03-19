package com.ssafy.tournest.travelNews.domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    private Long id;
    private String title;
    private LocalDate publishedDate;
    private String imageUrl;
    private String link; // 링크 추가
}

