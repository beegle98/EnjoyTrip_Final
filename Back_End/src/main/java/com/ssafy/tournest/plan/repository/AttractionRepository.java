package com.ssafy.tournest.plan.repository;

import com.ssafy.tournest.plan.domain.Attraction;
import com.ssafy.tournest.plan.domain.dto.AttractionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    @Query("select count(*) from Attraction")
    int countUser();

    @Query("select new com.ssafy.tournest.plan.domain.dto.AttractionDto(" +
            "a.id, a.title, a.addr1, a.latitude, a.longitude, count(l.id), a.first_image1)" +
            " from Attraction a left join Like l on a.id = l.id " +
            "where (:areaCode = 0 or a.area_code = :areaCode) " +
            "and (:title is null or a.title like %:title%) " +
            "and (:contentType = 0 or a.content_type_id = :contentType) " +
            "group by a.id")
    Page<AttractionDto> findAttractionsByFilter(@Param("areaCode") int areaCode,@Param("title") String title, @Param("contentType") int contentType,Pageable pageable);


}
