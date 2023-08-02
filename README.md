# Bad-Request

### Bad-Request 프로젝트는 질문 개발자 질문, 답변 커뮤니티 REST API Server

---

## 프로젝트 개발 및 운영 인원: 
#### Backend 
- ImBoriPapa: [gitghub](https://github.com/ImBoriPapa) 
#### Frontend
- EunjuHan: [github](https://github.com/eunju0209)

***

## Tech Stacks

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

## Features

### Version 1.0

- **JWT 토큰 기반 로그인 기능 및 로그아웃 기능 구현**
    - **Refresh Token을 사용한 보안과 사용자 인증을 강화하였습니다.**
    - **Redis를 사용하여 인증 정보를 분리하여 RDBMS의 I/O 작업을 최소화**
- **Jenkins와 Docker를 사용하여 Pipeline을 통한 자동 배포 구현**
- **Spring Rest Docs를 사용하여 end-point Test 및 API 문서 제공**
    - **엔드포인트 테스트를 용이하게 하고 사용자 및 개발자들에게 포괄적인 API 문서를 제공**
- **질문 게시글, 답변글, 댓글 CRUD API 구현**
    - **조회 로직을 분리하기 위해 Command Query Separation(CQS)을 적용하여 유지보수성 향상**
- **EventListener를 활용한 Service Layer의 의존성의 분리**
    - **코드 모듈성과 테스트 용이성을 개선하였습니다.**
- **쿼리 실행 계획을 확인하여 질문 게시글 조회수 및 추천 수 정렬 조회 최적화**

### Version 2.5  
- 회원 활동 내용 
  - 회원의 활동 내용을 저장 및 확인하는 기능을 구현하였습니다.
- 블로그 API   
  - 간단한 블로그 기능을 구현중입니다.

### Version 2.5 - Dev
- Domain-Driven-Design 적용중
- CQRS 고도화 작업
- Event 고도화 작업
- 테스트 고도화 


## [Show API Docs](https://www.bad-request.kr/docs/index.html)

---

## Project Architecture

![bad-request Project Architecture](https://user-images.githubusercontent.com/98242564/219410077-ff6967bc-be5f-43e8-8f01-2a9b4e294586.png)

### **Functional Flow Diagrams**

1. **Login and Request/Response Flow**
![로그인](https://github.com/ImBoriPapa/bori-market/assets/98242564/40ee8bb4-bc5d-4e4e-b0c6-9566262a6c2b)

3. **Reissuing JWT Token & Request-Response Flow**
![토큰 갱신](https://github.com/ImBoriPapa/bori-market/assets/98242564/62194eb6-88e5-4485-acda-cd004c7bf40c)

***

## ERD
![erd](https://github.com/ImBoriPapa/bori-market/assets/98242564/c9bb0210-7260-4280-927b-aa6858de533f)


