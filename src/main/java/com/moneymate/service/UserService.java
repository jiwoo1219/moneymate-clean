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

    /** 회원가입 + 추천인 보상 로직 */
    public User register(User user) {

        String ref = user.getReferrer();

        // 회원 먼저 저장 (ID가 있어야 pet 생성 가능)
        User savedUser = userRepository.save(user);

        // 추천인 처리
        if (ref != null && !ref.isBlank()) {

            User refUser = userRepository.findByUsername(ref);

            if (refUser != null) {
                // 추천인과 신규회원 모두 보상 (pet의 bones 증가)
                petService.addBones(refUser.getId(), 10);
                petService.addBones(savedUser.getId(), 10);
            } else {
                // 잘못된 추천인 → 초기화
                savedUser.setReferrer(null);
            }
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
