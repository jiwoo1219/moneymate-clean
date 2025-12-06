package com.moneymate.controller;

import com.moneymate.entity.Pet;
import com.moneymate.service.PetService;
import com.moneymate.service.PetStateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;
    private final Random random = new Random();

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /** 펫 조회 또는 생성 */
    @GetMapping("/{userId}")
    public ResponseEntity<Pet> getPet(@PathVariable Long userId) {
        Pet pet = petService.getOrCreatePet(userId);
        return ResponseEntity.ok(pet);
    }

    /** 프론트에서 petState 저장 */
    @PutMapping("/{userId}")
    public ResponseEntity<Pet> savePet(@PathVariable Long userId,
                                       @RequestBody PetStateDto dto) {
        Pet pet = petService.updatePetState(userId, dto);
        return ResponseEntity.ok(pet);
    }

    /** 강아지 이름 업데이트 */
    @PostMapping("/{userId}/name")
    public ResponseEntity<Pet> updateName(@PathVariable Long userId,
                                          @RequestBody Map<String, String> body) {
        String petName = body.get("petName");
        Pet pet = petService.updatePetName(userId, petName);
        return ResponseEntity.ok(pet);
    }

    /** 출석 체크 */
    @PostMapping("/{userId}/attendance")
    public ResponseEntity<?> attend(@PathVariable Long userId) {
        Pet pet = petService.attend(userId);
        if (pet == null) return ResponseEntity.status(409).body("already attended");
        return ResponseEntity.ok(pet);
    }

    /** 랜덤박스 */
    @PostMapping("/{userId}/random-box")
    public ResponseEntity<?> openRandomBox(@PathVariable Long userId) {
        int reward = random.nextInt(4); // 0~3
        Pet pet = petService.applyRandomBox(userId, reward);

        if (pet == null)
            return ResponseEntity.status(409).body("already opened");

        return ResponseEntity.ok(Map.of("reward", reward, "pet", pet));
    }

    /** 강제로 뼈다귀 증가 */
    @PostMapping("/{userId}/add-bones")
    public ResponseEntity<Pet> addBones(@PathVariable Long userId, @RequestParam int amount) {
        Pet pet = petService.addBones(userId, amount);
        return ResponseEntity.ok(pet);
    }

    /** 예산 초과 여부 계산 */
    @GetMapping("/{userId}/over-budget/{yearMonth}")
    public ResponseEntity<?> isOverBudget(
            @PathVariable Long userId,
            @PathVariable String yearMonth
    ) {
        boolean over = petService.isOverBudget(userId, yearMonth);
        return ResponseEntity.ok(Map.of("overBudget", over));
    }
}
