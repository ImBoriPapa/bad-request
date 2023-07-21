# Bad-Request

- Bad-Request 프로젝트는 프로그래밍에 대한 질문과 답변 댓글 API를 제공하는  REST API Server 입니다.

***
Version: 2.0.0
- JWT을 사용한 토큰 인증 기반 로그인 API를 제공합니다.
- OAUTH2를 사용한 Google,Github,Login 로그인 기능을 제공합니다.
- 질문 게시글, 답변, 댓글의 CRUD API을 제공합니다.
- 추천과 비추천 API

----

## 프로젝트 개발 및 운영 인원: 
#### Backend 
- boriPapa: [gitghub](https://github.com/ImBoriPapa) 
#### Frontend
- EunjuHan: [github](https://github.com/eunju0209)

***

## 사용 기술

- Java 11
- Spring Boot 2.7.7
- Spring Security
- Spring Data Jpa, Querydsl , Spring Data Redis, MariaDB
- Spring hateoas, Spring Rest Docs
- AWS EC2, AWS S3, N Cloud Server
- jenkins, docker, docker hub
- Git,GitHub
- Junit5, testcontainers

***

## 프로젝트 구조

![bad-request Project Architecture](https://user-images.githubusercontent.com/98242564/219410077-ff6967bc-be5f-43e8-8f01-2a9b4e294586.png)

***

## 구현 기능

### Version 1.0

| 기능                                       | 설명                                                                 |
|------------------------------------------|--------------------------------------------------------------------|
| 1. 배포 자동화                                | 프로젝트의 자동배포를 하기 위해 jenkins,docker,AWS EC2,Ncloud,GitHub를 사용하여 CI/CD |
| 2. Spring Security + JWT 토큰 기반 인증 로그인 구현 | Spring security+JWT를 사용한 로그인 기능을 구현하였습니다.                          |
| 3. Haetoas                               | Spring Haetoas 프로젝트를 사용하여 Haetoas 기능을 구현해 보았습니다.                   |
| 4. 이미지 업로드                               | AWS S3를 사용한 이미지 파일 저장기능을 구현하였습니다.                                  |
| 5. 질문,답변,댓글 CRUD                         | 주 기능인 질문,답변,댓글의 CRUD API를 구현했습니다.                                  |
| 6. API 문서화                               | Spring REST Docs를 사용하여 API문서를 작성하였습니다.                             |

### Version 2.0 - 구현중

| 기능          | 설명                                |
|-------------|-----------------------------------|
| 1. 회원 활동 내용 | 회원의 활동 내용을 저장 및 확인하는 기능을 구현하였습니다. |
| 2. 블로그 API  | 간단한 블로그 기능을 구현중입니다.               |

## Troubleshootings
[트러블 슈팅 문서 보기](https://github.com/ImBoriPapa/study-note/tree/main/troubleshooting/bad-request%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8)
1. Builder 패턴과 정적 팩토리 메서드 패턴을 활용한 엔티티 생성
2. EventListener를 활용한 Service Layer의 의존성과 관심사의 분리
3. 사용자 정의 예외클래스와 Enum을 활용한 에러응답
4. CI/CD 구축
*** 

## [API 문서 보기](https://www.bad-request.kr/docs/index.html)
***

## ERD
![bad-requestERD](https://github.com/ImBoriPapa/study-note/assets/98242564/e0a34e54-07a7-4f7c-95b0-2c4608cd0558)
