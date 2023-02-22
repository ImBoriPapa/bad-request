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

### API 문서: <https://www.bad-request.kr/docs/index.html>

***

### Tech Stack

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

### Project Architecture

![bad-request Project Architecture](https://user-images.githubusercontent.com/98242564/219410077-ff6967bc-be5f-43e8-8f01-2a9b4e294586.png)

***

### 주요 기능

| 기능                                       | 설명                                                                                    |
|------------------------------------------|---------------------------------------------------------------------------------------|
| 1. 배포자동화                                 | 프로젝트의 자동배포를 하기 위해 jenkins,docker,AWS EC2,Ncloud,GitHub를 사용하여 CI/CD                    |
| 2. Spring Security + JWT 토큰 기반 인증 로그인 구현 | Http의 Stateless 프로토콜을 구현하기 위해 Spring security+JWT를 사용한 로그인 기능을 구현하였습니다.               |
| 3. Haetoas                               | RESTful API Uniform InterfaceSpring를 만족시켜보기 위해 Spring Haetoas를 사용한 Haetoas 구현해 보았습니다. |
| 4. 로그 추적기                                | 운영자가 서버의 문제를 바로 확인할 수 있도록 AOP를 이용한 로그 추적기를 구현습니다.                                     |
| 5. SSE Protocl을 이용한 서버 상태 확인             | 서버의 상태를 실시간으로 확인하기 위해 SSE Protocl을 이용한 서버 상태 확인 기능을 구현하였습니다.                          |
| 6. 이미지 업로드                               | 리소스를 로컬이 아닌 전용 스토리지에 저장하기 위해 AWS S3에 이미지를 저장하는 기능을 구현하였습니다.                           |
| 7. 게시판,댓글,대댓글 조회                         | 사용자의 편의를 위해 데이터를 조건에 맞춰 분리해서 조회하기위해 JPA+QeuryDSL을 이용한 동적 데이터 조회기능을 구현했습니다.            |
| 8. API Document                          | 사용자에게 정확하고 읽기편한 api문서를 제공하기 위해 Spring REST Docs를 사용하여 API문서를 만들었습니다.                  |

### CI/CD

![ci-cd](https://user-images.githubusercontent.com/98242564/218456353-d969a6bc-9ae0-4678-ab63-47aee338c61f.png)

#### 1.프로젝트 commit and push to remote repository

![push](https://user-images.githubusercontent.com/98242564/218466542-7dbfa9f6-9056-4b53-a246-2e1d57a15271.png)

#### 2. jenkins는 jar 파일 build후 Docker image를 생성 Docker Hub 로  image Push

![jenkins-run](https://user-images.githubusercontent.com/98242564/218466672-2269e228-bbd4-4fb2-b880-6badde47cd97.png)

![jenkins-complete](https://user-images.githubusercontent.com/98242564/218466689-7a25727e-f703-4ce6-b34b-62eefc85d8fd.png)

#### jenkins pipeline

<pre>
<code>
pipeline {
    agent any

    environment {
        imagename = "boripapa/bad-request"
        registryCredential = 'bad-request-docker'
        dockerImage = ''
    }

    stages {
        stage('Prepare') {
          steps {
            echo 'Clonning Repository'
            git url: "git@github.com:ImBoriPapa/bad-request.git",
              branch: 'main',
              credentialsId: 'bad-request-git'
            }
            post {
             success { 
               echo 'Successfully Cloned Repository'
             }
           	 failure {
               error 'pipeline stops here Prepare..check logs'
             }
          }
        }

        stage('Bulid Gradle') {
          steps {
            echo 'Bulid Gradle!'
            dir('.'){
                sh './gradlew clean build'
            }
          }
          post {
            failure {
              error 'pipeline stops here Bulid Gradle..check logs'
            }
          }
        }
        
        stage('Bulid Docker') {
          steps {
            echo 'Bulid Docker!'
            script {
                dockerImage = docker.build imagename
            }
          }
          post {
            failure {
              error 'This pipeline stops here...Bulid Docker..check logs'
            }
          }
        }

        stage('Push Docker') {
          steps {
            echo 'Push Docker!'
            script {
                docker.withRegistry( 'https://registry.hub.docker.com', registryCredential) {
                    dockerImage.push() 
                }
            }
          }
          post {
            failure {
              error 'This pipeline stops here Docker Push...check logs'
            }
          }
        }
        
        stage('Docker Run') {
            steps {
                echo 'Pull Docker Image & Docker Image Run'
                sshagent (credentials: ['bad-request-aws']) {
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@xx.xxx.xxx.xxx 'docker pull boripapa/bad-request'" 
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@xx.xxx.xxx.xxx 'docker rm -f jenkins'"
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@xx.xxx.xxx.xxx 'docker run -d --name jenkins -p 8080:8080  -v /home/ec2-user/yml:/home boripapa/bad-request'"
                }
            }
        }
    }
    
}
</code>
</pre>

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

#### 데이터 수집
![스크린샷 2023-02-22 오후 1 33 32](https://user-images.githubusercontent.com/98242564/220522743-3522ffd1-c2e2-4f48-ac6e-68d54407db1c.png)
#### event 발행
![스크린샷 2023-02-22 오후 1 32 02](https://user-images.githubusercontent.com/98242564/220522579-753e06c9-71e2-491e-b34f-b7fd3fa80982.png)

#### sse connection
![스크린샷 2023-02-22 오후 1 35 56](https://user-images.githubusercontent.com/98242564/220523115-baad37b0-c07f-470c-b624-69aff3124e75.png)

#### client event 구독
![sse-sample](https://user-images.githubusercontent.com/98242564/218492461-4b34dc13-a84f-409a-ae45-61ed015c5912.gif)
***

### AOP를 이용한 CustomLogTrace를 사용해서 Log 추적 API 제공
#### 추적을 원하는 로직에 @CustomLogTracer 애너테이션 사용
![스크린샷 2023-02-22 오후 1 40 21](https://user-images.githubusercontent.com/98242564/220523664-5a71a8f5-ac9f-477c-9760-fbe14c8ae97c.png)

#### log 수집 및 저장
![스크린샷 2023-02-22 오후 1 37 52](https://user-images.githubusercontent.com/98242564/220523368-6d530a93-4c36-441b-98a2-51efa6f5eeab.png)

#### 로그 조회 
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
