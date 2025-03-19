package com.ssafy.tournest.travelNews.repository;

import com.ssafy.tournest.travelNews.domain.Dto.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByPublishedDate(LocalDate publishedDate);
    boolean existsByTitleAndPublishedDate(String title, LocalDate publishedDate);
}
