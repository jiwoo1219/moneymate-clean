package com.moneymate.service;

import com.moneymate.entity.User;
import com.moneymate.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ⭐ 회원가입 + 추천인 보상 로직
    public User register(User user) {

        // 기본 bones 초기화
        user.setBones(0);

        String ref = user.getReferrer();

        // 추천인 아이디가 존재한다면 처리
        if (ref != null && !ref.isEmpty()) {

            // 추천인 유저 찾기
            User refUser = userRepository.findByUsername(ref);

            if (refUser != null) {
                // 추천인 존재 → 양쪽 모두 보상 +10
                refUser.setBones(refUser.getBones() + 10);
                user.setBones(user.getBones() + 10);

                // 추천인 저장
                userRepository.save(refUser);

            } else {
                // 추천인 아이디 잘못된 경우 → 추천인 무효화
                user.setReferrer(null);
            }
        }

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
