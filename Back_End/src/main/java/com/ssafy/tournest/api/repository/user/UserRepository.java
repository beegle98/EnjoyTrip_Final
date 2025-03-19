package com.ssafy.tournest.api.repository.user;

import com.ssafy.tournest.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);
    User findByEmail(String email);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}
