package com.ssafy.tournest.travelNews.service;

import com.ssafy.tournest.travelNews.domain.Dto.News;
import com.ssafy.tournest.travelNews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NewsCrawlerService {
    private final NewsRepository newsRepository;

    public void crawlAndSaveNews() throws IOException {
        String url = "https://www.traveltimes.co.kr/";
        Document doc = Jsoup.connect(url).get();

        // id="skin-55" 아래의 item 클래스 선택
        Elements articles = doc.select("#skin-55 .item");

        for (Element article : articles) {
            // 제목 추출
            String title = article.select(".auto-titles").text();

            // 이미지 URL 추출
            String imageUrl = article.select(".auto-images").attr("style");
            imageUrl = extractImageUrl(imageUrl);

            // 기사 링크 추출
            String link = article.select("a").attr("href");
            if (!link.startsWith("http")) {
                link = "https://www.traveltimes.co.kr" + link; // 상대 경로를 절대 경로로 변환
            }

            // 날짜는 오늘 날짜로 설정
            LocalDate publishedDate = LocalDate.now();

            // 중복 방지: 이미 저장된 뉴스인지 확인
            if (!newsRepository.existsByTitleAndPublishedDate(title, publishedDate)) {
                News news = new News();
                news.setTitle(title);
                news.setPublishedDate(publishedDate);
                news.setImageUrl(imageUrl);
                news.setLink(link);
                newsRepository.save(news);
            }
        }
    }

    // style 속성에서 이미지 URL 추출
    private String extractImageUrl(String style) {
        String prefix = "background-image:url(";
        String suffix = ")";
        if (style.contains(prefix) && style.contains(suffix)) {
            int start = style.indexOf(prefix) + prefix.length();
            int end = style.indexOf(suffix, start);
            return style.substring(start, end);
        }
        return "";
    }
}
