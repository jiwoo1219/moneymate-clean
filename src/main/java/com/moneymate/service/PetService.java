package com.moneymate.service;

import com.moneymate.entity.Pet;
import com.moneymate.entity.User;
import com.moneymate.entity.Budget;
import com.moneymate.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public PetService(PetRepository petRepository,
                      UserRepository userRepository,
                      BudgetRepository budgetRepository,
                      ExpenseRepository expenseRepository) {

        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    /** 펫 조회 또는 생성 */
    public Pet getOrCreatePet(Long userId) {
        return petRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));

                    Pet pet = new Pet();
                    pet.setUser(user);
                    return petRepository.save(pet);
                });
    }

    /** 이름 변경 */
    public Pet updatePetName(Long userId, String petName) {
        Pet pet = getOrCreatePet(userId);
        pet.setPetName(petName);
        return petRepository.save(pet);
    }

    /** 상태 업데이트 */
    public Pet updatePetState(Long userId, PetStateDto dto) {
        Pet pet = getOrCreatePet(userId);
        pet.setLevel(dto.getLevel());
        pet.setXp(dto.getXp());
        pet.setBones(dto.getBones());
        pet.setLastCheckDate(dto.getLastCheckDate());
        pet.setLastBoxDate(dto.getLastBoxDate());
        return petRepository.save(pet);
    }

    /** 출석 체크 */
    public Pet attend(Long userId) {
        Pet pet = getOrCreatePet(userId);
        LocalDate today = LocalDate.now();

        if (today.equals(pet.getLastCheckDate())) return null;

        pet.setLastCheckDate(today);
        pet.setBones(pet.getBones() + 1);

        return petRepository.save(pet);
    }

    /** 랜덤박스 */
    public Pet applyRandomBox(Long userId, int reward) {
        Pet pet = getOrCreatePet(userId);

        LocalDate today = LocalDate.now();
        LocalDate last = pet.getLastBoxDate();

        if (last != null && last.equals(today)) return null;

        pet.setLastBoxDate(today);
        pet.setBones(pet.getBones() + reward);

        return petRepository.save(pet);
    }

    /** 강제로 뼈다귀 추가 */
    public Pet addBones(Long userId, int amount) {
        Pet pet = getOrCreatePet(userId);
        pet.setBones(pet.getBones() + amount);
        return petRepository.save(pet);
    }

   public boolean isOverBudget(Long userId, String yearMonth) {

    // yearMonth = "2025-12" 같은 형식 → year, month로 분리
    String[] parts = yearMonth.split("-");
    int year = Integer.parseInt(parts[0]);
    int month = Integer.parseInt(parts[1]);

    // 예산 조회
    int budget = budgetRepository
            .findByUser_IdAndYearAndMonth(userId, year, month)
            .map(Budget::getTotalBudget)
            .orElse(0);

    // 지출 조회
    int spent = expenseRepository.sumMonthlyExpense(userId, yearMonth);

    // 초과 여부 반환
    return spent > budget;
}

}
