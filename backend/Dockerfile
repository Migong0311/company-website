# 1. Base Image: Java 17을 실행할 수 있는 가벼운 리눅스 환경(Alpine)
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너 내부로 복사
# (주의: 로컬에서 먼저 빌드를 해야 이 파일이 생깁니다)
COPY build/libs/*.jar app.jar

# 4. 컨테이너 실행 시 작동할 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]