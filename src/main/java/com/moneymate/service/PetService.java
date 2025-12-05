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

    /** 펫 상태 저장 */
    public Pet updatePetState(Long userId, PetStateDto dto) {
        Pet pet = getOrCreatePet(userId);
        pet.setLevel(dto.getLevel());
        pet.setXp(dto.getXp());
        pet.setBones(dto.getBones());
        pet.setLastCheckDate(dto.getLastCheckDate());
        pet.setLastBoxDate(dto.getLastBoxDate());
        return petRepository.save(pet);   // ← 추가됨
    }

    /** 출석 체크 */
    public Pet attend(Long userId) {
        Pet pet = getOrCreatePet(userId);
        LocalDate today = LocalDate.now();

        if (today.equals(pet.getLastCheckDate())) {
            return null;
        }

        pet.setLastCheckDate(today);
        pet.setBones(pet.getBones() + 1);

        return petRepository.save(pet);  // ← 반드시 필요
    }

       /** 랜덤박스 */
    public Pet applyRandomBox(Long userId, int reward) {
        Pet pet = getOrCreatePet(userId);
    
        LocalDate today = LocalDate.now();
        LocalDate last = pet.getLastBoxDate();
    
        if (last != null && last.equals(today)) {
            return null;  // 오늘 이미 함
        }
    
        pet.setLastBoxDate(today);
        pet.setBones(pet.getBones() + reward);
    
        return petRepository.save(pet);
    }


    /** 관리자용 / 추천인 보상 */
    public Pet addBones(Long userId, int amount) {
        Pet pet = getOrCreatePet(userId);
        pet.setBones(pet.getBones() + amount);
        return petRepository.save(pet); // ← 반드시 필요
    }
}

