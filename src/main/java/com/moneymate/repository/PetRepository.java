package com.moneymate.repository;

import com.moneymate.entity.Pet;
import com.moneymate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByUser(User user);

    Optional<Pet> findByUserId(Long userId);
}
