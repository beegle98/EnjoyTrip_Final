package com.ssafy.tournest.notice.repository;

import com.ssafy.tournest.notice.domain.Notice;
import com.ssafy.tournest.notice.domain.dto.NoticeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("SELECT n FROM Notice n WHERE " +
            "(:key = 'notice_no' AND CAST(n.noticeNo AS string) LIKE %:word%) OR " +
            "(:key = 'subject' AND n.subject LIKE %:word%) OR " +
            "(:key = '')")
    Page<Notice> findBySearchCondition(
            @Param("key") String key,
            @Param("word") String word,
            Pageable pageable
    );
}
