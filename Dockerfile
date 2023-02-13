FROM openjdk:11-jdk

# JAR_FILE 변수 정의 -> 기본적으로 jar file이 2개이기 때문에 이름을 특정해야함
ARG JAR_FILE=./build/libs/bad-request-1.0.0-PROD.jar

# JAR 파일 메인 디렉토리에 복사
COPY ${JAR_FILE} bad-request.jar

# 시스템 진입점 정의
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/bad-request.jar"]

