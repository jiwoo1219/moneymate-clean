package com.moneymate.repository;

import com.moneymate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // username으로 찾기
    User findByUsername(String username);

    // username 존재 여부 확인용
    boolean existsByUsername(String username);
}
