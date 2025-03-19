package com.ssafy.tournest.attractionboard.repository;

import com.ssafy.tournest.plan.domain.AttractionNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttractionNewsRepository extends JpaRepository<AttractionNews, Long> {

    @Query("SELECT distinct(a) " +
            "FROM AttractionNews a " +
            "WHERE a.attrName = :attractionId")
    AttractionNews findByAttrName(@Param("attractionId") String attrName);
}
