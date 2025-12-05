package com.moneymate.service;

import com.moneymate.entity.Pet;
import com.moneymate.entity.User;
import com.moneymate.repository.PetRepository;
import com.moneymate.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public Pet getOrCreatePet(Long userId) {
    return petRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                Pet pet = new Pet();
                pet.setUser(user);
                pet.setLevel(1);
                pet.setXp(0);
                pet.setBones(0);
                pet.setLastCheckDate(null);
                pet.setLastBoxDate(null);

                return petRepository.save(pet);
            });
    }


    public Pet updatePetState(Long userId, PetStateDto dto) {
        Pet pet = getOrCreatePet(userId);
        pet.setLevel(dto.getLevel());
        pet.setXp(dto.getXp());
        pet.setBones(dto.getBones());
        pet.setLastCheckDate(dto.getLastCheckDate());
        pet.setLastBoxDate(dto.getLastBoxDate());
        return pet;
    }

    /** 출석 체크: 오늘 이미 했으면 null 반환, 아니면 +1 후 Pet 반환 */
    public Pet attend(Long userId) {
        Pet pet = getOrCreatePet(userId);
        LocalDate today = LocalDate.now();
        if (today.equals(pet.getLastCheckDate())) {
            return null; // 이미 출석
        }
        pet.setLastCheckDate(today);
        pet.setBones(pet.getBones() + 1);
        return pet;
    }

    /** 랜덤박스 보상 (보상 계산은 프론트에서 해도 되고, 여기서 해도 됨) */
    public Pet applyRandomBox(Long userId, int reward) {
        Pet pet = getOrCreatePet(userId);
        LocalDate today = LocalDate.now();
        if (today.equals(pet.getLastBoxDate())) {
            return null; // 이미 열었음
        }
        pet.setLastBoxDate(today);
        pet.setBones(pet.getBones() + reward);
        return pet;
    }

    /** 테스트용: 강아지 뼈다귀 마음대로 넣기 */
    public Pet addBones(Long userId, int amount) {
        Pet pet = getOrCreatePet(userId);
        pet.setBones(pet.getBones() + amount);
        return pet;
    }
}
