package com.ssafy.tournest.travelNews.service;


import com.ssafy.tournest.travelNews.domain.Dto.News;
import com.ssafy.tournest.travelNews.domain.Dto.NewsDto;
import com.ssafy.tournest.travelNews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsCrawlerService newsCrawlerService;

    public List<NewsDto> getTodayNews() throws IOException {
        LocalDate today = LocalDate.now();

        List<News> todayNews = newsRepository.findByPublishedDate(today);

        if (todayNews.isEmpty()) {
            newsCrawlerService.crawlAndSaveNews();
            todayNews = newsRepository.findByPublishedDate(today);
        }

        // 엔티티를 DTO로 변환
        return todayNews.stream()
                .map(news -> new NewsDto(
                        news.getId(),
                        news.getTitle(),
                        news.getPublishedDate(),
                        news.getImageUrl(),
                        news.getLink() // 링크 추가
                ))
                .collect(Collectors.toList());
    }
}
