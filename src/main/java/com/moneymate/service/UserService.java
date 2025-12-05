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

        // 1) 회원 먼저 저장 (User ID 필요)
        User savedUser = userRepository.save(user);

        // 2) 추천인 로직 처리
        if (ref != null && !ref.isBlank()) {

            User refUser = userRepository.findByUsername(ref);

            // 추천인 존재 + 자기 자신이 아닌 경우만 처리
            if (refUser != null && !refUser.getId().equals(savedUser.getId())) {

                // 각각 Pet에 보상 10개 추가
                petService.addBones(refUser.getId(), 10);
                petService.addBones(savedUser.getId(), 10);

            } else {
                // 잘못된 추천인 → referrer 초기화 + DB 저장
                savedUser.setReferrer(null);
                userRepository.save(savedUser);
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
