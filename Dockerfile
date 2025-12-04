# 1단계: 빌드 이미지
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Maven Wrapper 및 소스 복사
COPY . .

# Maven Wrapper 실행 권한 부여
RUN chmod +x mvnw

# JAR 빌드 (테스트 스킵)
RUN ./mvnw clean package -DskipTests

# 2단계: 런타임 이미지
FROM eclipse-temurin:17-jre

WORKDIR /app

# builder 단계에서 생성된 JAR 파일 복사
COPY --from=builder /app/target/moneymate-0.0.1-SNAPSHOT.jar app.jar

# 8080 포트
EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
