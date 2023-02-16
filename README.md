# Bad-Request

### Bad-Request 프로젝트는 개발자 커뮤니티 웹 REST API Server 입니다.

***
Version: 1.0.0

- 개발자들이 자유롭게 질문,지식공유,자유토론을 할 수 있는 게시판 기능 API 제공 서버입니다.
- JWT을 사용한 토큰 인증 기반 로그인 API를 제공합니다.
- 게시판에 이미지 첨부와 댓글 기능 API를 제공합니다.
- 개발자들 운영 서버에 로그 기록과 환경을 간단히 확인할 수 있는 API를 제공합니다.

----

### 프로젝트 개발 및 운영 인원: 백엔드 1명 프론트엔드 1명

### 클라이언트 서버: <https://>(개발중)

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
***
### Project Architecture

![bad-request Project Architecture](https://user-images.githubusercontent.com/98242564/219410077-ff6967bc-be5f-43e8-8f01-2a9b4e294586.png)

***

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
***
## API Example
### 로그인
POST: <https://www.bad-request.kr/api/v1/login>

- 요청

![스크린샷 2023-02-14 오후 2 50 21 작게](https://user-images.githubusercontent.com/98242564/218651033-7b20b805-6aea-441b-a62a-1a8939a7dc16.png)

- 응답

![스크린샷 2023-02-14 오후 2 52 38](https://user-images.githubusercontent.com/98242564/218651296-7e62466d-a4ba-45cc-a7d7-83755c0d7ae8.png)

***
### 게시판 목록 조회
- 요청

![스크린샷 2023-02-14 오후 2 56 13 작게](https://user-images.githubusercontent.com/98242564/218651888-e53d65aa-9c4f-442e-9cc8-a4965749cc61.png)

- 응답

![스크린샷 2023-02-14 오후 2 59 57](https://user-images.githubusercontent.com/98242564/218652474-9263d1a0-3a45-4066-9850-c69ed763ba15.png)


***
### 댓글 조회
- 요청

![스크린샷 2023-02-14 오후 11 08 17](https://user-images.githubusercontent.com/98242564/218762232-99ab3b2c-f379-427a-a090-fb051d06b5d2.png)
- 응답

![스크린샷 2023-02-14 오후 11 10 35](https://user-images.githubusercontent.com/98242564/218762843-d4725be3-f9a6-4944-867a-29e65f7299b8.png)
***
### SSE Protocol을 활용한 실시간 단방향 시스템 정보 API 제공 (5초 간격으로 데이터 갱신)

#### Example

![sse-sample](https://user-images.githubusercontent.com/98242564/218492461-4b34dc13-a84f-409a-ae45-61ed015c5912.gif)
***
### AOP를 이용한 CustomLogTrace를 사용해서 Log 추적 API 제공

<pre>
<code>
{
    "result": [
        {
            "id": 2,
            "logTime": "2023-02-14T00:05:29.473411",
            "logLevel": "INFO",
            "className": "BoardQueryController",
            "methodName": "getBoardList",
            "message": "[com.study.badrequest.domain.board.dto.BoardSearchCondition@66ab03fd, org.springframework.validation.BeanPropertyBindingResult: 0 errors]",
            "requestURI": "/api/v1/board",
            "clientIp": "0:0:0:0:0:0:0:1",
            "username": "anonymousUser",
            "stackTrace": "NO TRACE"
        },
        {
            "id": 1,
            "logTime": "2023-02-14T00:05:06.878091",
            "logLevel": "INFO",
            "className": "DashBoardController",
            "methodName": "getLogs",
            "message": "[30, null, null, null, null]",
            "requestURI": "/log",
            "clientIp": "0:0:0:0:0:0:0:1",
            "username": "anonymousUser",
            "stackTrace": "NO TRACE"
        }
            ]
}
</code>
</pre>
***
