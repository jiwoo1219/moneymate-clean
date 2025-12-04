package com.moneymate.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;  // 아이디

    private String password;  // 비밀번호
    private String name;      // 이름
    private int age;          // 나이
    private String gender;    // 성별

    // ⭐ 추천인 아이디 (선택)
    private String referrer;

    // ⭐ 뼈다귀 개수 추가
    private int bones = 0;

    // 기본 생성자
    public User() {}

    // 전체 생성자
    public User(String username, String password, String name, int age, String gender, String referrer) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.referrer = referrer;
    }

    // Getter/Setter
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }

    public int getBones() { return bones; }
    public void setBones(int bones) { this.bones = bones; }
}
