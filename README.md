# Bad-Request


### Bad-Request 프로젝트는 개발자 커뮤니티 REST API Server 입니다.
***
 Version: 1.0.0

- 개발자들이 자유롭게 질문,지식공유,자유토론을 할 수 있는 게시판 기능 API 제공 서버입니다.
- JWT을 사용한 토큰 인증 기반 로그인 API를 제공합니다.
- 게시판에 이미지 첨부와 댓글 기능 API를 제공합니다.
- 개발자들 운영 서버에 로그 기록과 환경을 간단히 확인할 수 있는 API를 제공합니다.

----

### 프로젝트 개발 및 운영 인원: 백엔드 1명 프론트엔드 1명

### 클라이언트 서버: <https://not-yet.com>(개발중)

***

### API 문서: <http://www.bad-request.kr/docs/index.html>

***

### Tech Stack

- Java 11
- Spring Boot
- Spring Security
- Spring Data Jpa, Querydsl , Spring Data Redis, MariaDb
- Spring hateoas, Spring Rest Docs
- AWS EC2, AWS S3, N Cloud Server
- jenkins, docker, docker hub
- Git,GitHub
- Junit5, testcontainers

### CI/CD

![ci-cd](https://user-images.githubusercontent.com/98242564/218456353-d969a6bc-9ae0-4678-ab63-47aee338c61f.png)

#### 1.프로젝트 commit and push to remote repository
![push](https://user-images.githubusercontent.com/98242564/218466542-7dbfa9f6-9056-4b53-a246-2e1d57a15271.png)

#### 2. jenkins는 jar 파일 build후 Docker image를 생성 Docker Hub 로  image Push
![jenkins-run](https://user-images.githubusercontent.com/98242564/218466672-2269e228-bbd4-4fb2-b880-6badde47cd97.png)


![jenkins-complete](https://user-images.githubusercontent.com/98242564/218466689-7a25727e-f703-4ce6-b34b-62eefc85d8fd.png)

#### 3. 운영 서버에서 도커이미지를 pull -> jar파일 실행
<pre>
<code>
FROM openjdk:11-jdk

# JAR_FILE 변수 정의 -> 기본적으로 jar file이 2개이기 때문에 이름을 특정해야함
ARG JAR_FILE=./build/libs/bad-request-1.0.0-PROD.jar

# JAR 파일 메인 디렉토리에 복사
COPY ${JAR_FILE} bad-request.jar

# 시스템 진입점 정의
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/bad-request.jar"]
</code>
</pre>

