package com.ssafy.tournest.travelNews.controller;

import com.ssafy.tournest.travelNews.domain.Dto.NewsDto;
import com.ssafy.tournest.travelNews.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class TravelNewsController {
    private final NewsService newsService;

    @GetMapping("/today")
    public ResponseEntity<List<NewsDto>> getTodayNews() {
        try {
            List<NewsDto> news = newsService.getTodayNews();
            return ResponseEntity.ok(news);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}