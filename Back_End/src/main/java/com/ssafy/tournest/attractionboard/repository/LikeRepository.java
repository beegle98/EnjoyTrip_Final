package com.ssafy.tournest.attractionboard.repository;

import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.attractionboard.domain.Like;
import com.ssafy.tournest.plan.domain.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.attraction.id = :attractionId")
    Optional<Like> findLikeByUserIdAndAttractionId(@Param("userId") Long userId, @Param("attractionId") Long attractionId);

    Optional<Like> findByUserAndAttraction(User user, Attraction attraction);

    @Query("SELECT COUNT(l) " +
            "FROM Like l " +
            "WHERE l.attraction.id = :attractionId " +
            "GROUP BY l.attraction.id")
    Long countLikesByAttractionIdWithGroupBy(@Param("attractionId") Long attractionId);

    @Query("SELECT l.attraction FROM Like l WHERE l.user.id = :userId")
    List<Attraction> findLikedAttractionsByUserId(@Param("userId") Long userId);
}
