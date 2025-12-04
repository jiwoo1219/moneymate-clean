package com.moneymate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.moneymate.entity.Pet;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // owner(User)의 id로 조회
	List<Pet> findByUserId(Long userId);

    // 이름 검색(선택)
	List<Pet> findByUserIdAndNameContaining(Long userId, String name);

}

