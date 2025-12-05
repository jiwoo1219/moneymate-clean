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
     * 회원가입 + 추천인 보상 로직
     *
     * - 추천코드 형식: "REF{userId}"
     *   예) "REF3" → id = 3인 유저가 추천인
     * - 추천코드가 잘못되었거나, 자기 자신을 추천한 경우 referrer 를 null 로 저장
     * - 유효한 추천인인 경우:
     *      - 추천인 Pet 뼈다귀 +10
     *      - 신규 가입자 Pet 뼈다귀 +10
     */
    public User register(User user) {

        // 회원가입 시 입력된 추천코드 (그냥 raw 값)
        String ref = user.getReferrer();

        // 1) 회원 먼저 저장 (User ID 필요)
        User savedUser = userRepository.save(user);

        // 2) 추천인 로직 처리 (referrer 없으면 바로 반환)
        if (ref == null || ref.isBlank()) {
            return savedUser;
        }

        ref = ref.trim();

        // 추천코드 형식이 "REF"로 시작하지 않으면 잘못된 코드로 처리
        if (!ref.startsWith("REF")) {
            savedUser.setReferrer(null);
            userRepository.save(savedUser);
            return savedUser;
        }

        try {
            // "REF" 이후 숫자 부분만 잘라서 userId 로 사용
            Long refId = Long.parseLong(ref.substring(3));

            // 자기 자신 추천 방지
            if (refId.equals(savedUser.getId())) {
                savedUser.setReferrer(null);
                userRepository.save(savedUser);
                return savedUser;
            }

            // 실제 추천인 조회
            User refUser = userRepository.findById(refId).orElse(null);

            if (refUser != null) {
                // ✅ 유효한 추천인인 경우: 둘 다 뼈다귀 10개 보상
                petService.addBones(refUser.getId(), 10);
                petService.addBones(savedUser.getId(), 10);
                // referrer 문자열(REF번호)은 그대로 두어도 되고,
                // 나중에 통계용으로 사용 가능
            } else {
                // 추천인 ID가 존재하지 않으면 잘못된 코드이므로 referrer 제거
                savedUser.setReferrer(null);
                userRepository.save(savedUser);
            }

        } catch (NumberFormatException e) {
            // "REF" 뒤에 숫자가 아닌 값이 들어온 경우 → 잘못된 코드로 처리
            savedUser.setReferrer(null);
            userRepository.save(savedUser);
        }

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
