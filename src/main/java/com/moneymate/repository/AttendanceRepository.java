package com.moneymate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.moneymate.entity.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 사용자별 출퇴근 로그
	List<Attendance> findByUserIdOrderByDateDesc(Long userId);

    // 기간 필터 (선택)
	List<Attendance> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}


