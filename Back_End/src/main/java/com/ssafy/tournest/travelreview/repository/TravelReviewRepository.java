package com.ssafy.tournest.travelreview.repository;

import com.ssafy.tournest.travelreview.domain.TravelReview;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelReviewRepository extends JpaRepository<TravelReview, Long> {
    @Query("SELECT t FROM TravelReview t WHERE " +
            "(:key = 'location' AND t.location LIKE %:word%) OR " +
            "(:key = 'subject' AND t.subject LIKE %:word%) OR " +
            "(:key = '')")
    Page<TravelReview> findBySearchCondition(
            @Param("key") String key,
            @Param("word") String word,
            Pageable pageable
    );
    List<TravelReview> findByUserId(String userId);

    // 선택적: 작성 시간 기준 내림차순 정렬
    List<TravelReview> findByUserIdOrderByTravelReviewNoDesc(String userId);
}
