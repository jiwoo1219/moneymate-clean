package com.moneymate.service;

import com.moneymate.entity.User;
import com.moneymate.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PetService petService;

    public UserService(UserRepository userRepository, PetService petService) {
        this.userRepository = userRepository;
        this.petService = petService;
    }

    /**
     * 회원가입 + 추천인 보상 로직 (username 기반)
     *
     * - 추천인은 기존의 "REF{id}" 방식이 아니라
     *   "사용자 아이디(username)" 그대로 받는 방식으로 변경됨.
     *
     * - 추천인(username)이 존재하고, 자기 자신이 아닐 경우:
     *     → 추천인 Pet 뼈다귀 +10
     *     → 신규 가입자 Pet 뼈다귀 +10
     *
     * - 추천인이 없다면 그대로 가입 처리
     * - 추천인이 잘못된 username이면 referrer=null로 저장
     */
    public User register(User user) {

        // 입력된 추천인 username (예: "hwangjiwoo")
        String ref = user.getReferrer();

        // 1) 회원부터 저장 (ID 필요)
        User savedUser = userRepository.save(user);

        // 추천인 미입력 시 바로 반환
        if (ref == null || ref.isBlank()) {
            return savedUser;
        }

        ref = ref.trim(); // 스페이스 제거

        // 추천인 username 조회
        User refUser = userRepository.findByUsername(ref);

        // 추천인이 존재하고, 자기 자신이 아닌 경우만 보상
        if (refUser != null && !refUser.getId().equals(savedUser.getId())) {

            // 추천인에게 보상
            petService.addBones(refUser.getId(), 10);

            // 신규 가입자에게도 보상
            petService.addBones(savedUser.getId(), 10);

            return savedUser;
        }

        // 추천인이 존재하지 않거나 자기 자신 추천 → 무효 처리
        savedUser.setReferrer(null);
        userRepository.save(savedUser);

        return savedUser;
    }

    /** 로그인 */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
