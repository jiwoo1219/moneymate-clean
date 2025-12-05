package com.moneymate.controller;

import com.moneymate.entity.Pet;
import com.moneymate.service.PetService;
import com.moneymate.service.PetStateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;
    private final Random random = new Random();

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Pet> getPet(@PathVariable Long userId) {
        Pet pet = petService.getOrCreatePet(userId);
        return ResponseEntity.ok(pet);
    }

    /** 프론트에서 계산한 dogState 전체 저장용 (최소 변경 버전) */
    @PutMapping("/{userId}")
    public ResponseEntity<Pet> savePet(@PathVariable Long userId,
                                       @RequestBody PetStateDto dto) {
        Pet pet = petService.updatePetState(userId, dto);
        return ResponseEntity.ok(pet);
    }

    /** 출석 체크: 이미 했으면 409, 아니면 200 */
    @PostMapping("/{userId}/attendance")
    public ResponseEntity<?> attend(@PathVariable Long userId) {
        Pet pet = petService.attend(userId);
        if (pet == null) {
            return ResponseEntity.status(409).body("already attended");
        }
        return ResponseEntity.ok(pet);
    }

    /** 랜덤박스 – 서버에서 보상 계산하고 저장 */
    @PostMapping("/{userId}/random-box")
    public ResponseEntity<?> openRandomBox(@PathVariable Long userId) {
        int reward = random.nextInt(4); // 0~3
        Pet pet = petService.applyRandomBox(userId, reward);
        if (pet == null) {
            return ResponseEntity.status(409).body("already opened");
        }
        return ResponseEntity.ok(new RandomBoxResponse(reward, pet));
    }

    /** 시연용: 마음대로 뼈다귀 넣기 */
    @PostMapping("/{userId}/add-bones")
    public ResponseEntity<Pet> addBones(@PathVariable Long userId,
                                        @RequestParam int amount) {
        Pet pet = petService.addBones(userId, amount);
        return ResponseEntity.ok(pet);
    }

    record RandomBoxResponse(int reward, Pet pet) {}
}
