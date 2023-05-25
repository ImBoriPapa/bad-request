# Bad-Request

### Bad-Request 프로젝트는 개발자 커뮤니티 웹 REST API Server 입니다.

***
Version: 2.0.0

- 개발자들이 자유롭게 질문,지식공유,자유토론을 할 수 있는 게시판 기능 API 제공 서버입니다.
- JWT을 사용한 토큰 인증 기반 로그인 API를 제공합니다.
- OAUTH2를 사용한 Google,Github,Login 로그인 기능을 제공합니다.
- 질문을 등록하고 수정 삭제 조회 기능을 제공합니다.
- 답변을 등록하고 수정 삭제 조회 기능을 제공합니다.
- 추천과 비추천을 할 수 있습니다.
- 답변에 댓글 기능을 추가하였습니다.

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



## ERD
