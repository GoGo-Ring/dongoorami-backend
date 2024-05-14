# dongoorami-backend

> 동구라미🙆, 함께 공연을 즐길 동행인을 찾는 이들을 위한 특별한 서비스

## 🔗 API 문서 및 서비스 링크

- [RestDocs](https://www.dongoorami.shop:8080/docs/index.html)
- [동구라미 서비스](https://dongoorami.netlify.app/)

## 👀 프로젝트 소개

공연을 함께 즐길 동행자, 이동 동행자, 숙소 동행자까지 공연 관람의 A to Z를 계획하는데 도움을 주는 서비스를 제공하여 궁극적으로 공연 관람에 즐거움을 더합니다.

### 기획 의도
- 공연을 관람하는 사람들이 SNS라는 분리된 도메인에서 동행을 구인한다는 사실 확인
- SNS에서 구인할 시 동행이라는 키워드 하나로 관리되지 않은 일정과 뒤섞인 게시물 중에서 믿을만한 사용자를 찾아야 한다는 문제점 확인
- 현재 콘서트나 페스티벌 등의 공연 동행을 구할 수 있는 서비스를 찾아볼 수 없다는 점에 주목

### 주요 기능

- 공연 목록 확인

  - 현재 진행 중인 다양한 공연 정보 목록을 확인할 수 있습니다.
  - 공연 목록을 필터링하여 원하는 공연만 검색할 수 있습니다.
  - 다른 사람들이 남긴 공연 후기를 확인할 수 있습니다.

- 동행 구인 서비스

  - 공연 및 콘서트를 함께 동행하기 위한 게시글을 작성할 수 있습니다.
  - 원하는 조건에 따라 동행 구인 게시물을 확인할 수 있습니다.
  - 쪽지를 통해 대화를 나눌 수 있습니다.
  - 동행 구인 글을 필터링하여 관심 있는 정보만 추려 낼 수 있습니다.
  - 원하는 동행 구인 글을 찜할 수 있습니다.

- 동행 후기 서비스

  - 함께 동행한 사람들의 매너 점수, 후기를 입력할 수 있습니다.
  - 다른 사람들이 남긴 동행 후기를 확인할 수 있습니다.

## 🌟 개발 포인트

- CI/CD 구축
- 커서 기반 무한 스크롤
- 필터링 QueryDsl 적극 활용
- 동시성 처리
- Open API를 활용한 공연 정보 업데이트
- Spring Acutator를 통한 로그 확인
- 테스트 코드 작성

## 💪 백엔드 팀원

|  Name   |             [최정은](https://github.com/JeongeunChoi)              |             [이유정](https://github.com/letskuku)       | 
|:-------:|:---------------------------------------------------------------:|:----------------------------------------------------------:|
| Profile | <img width="100px" src="https://github.com/JeongeunChoi.png" /> | <img width="100px" src="https://github.com/letskuku.png" /> |
|  Role   |                          AWS 책임자, 동행/쪽지 API                           |                       팀장, 공연/회원/찜 API                        |  

## 🦀 서버 구성
![cicdaws서버](https://github.com/GoGo-Ring/dongoorami-backend/assets/77786996/a5b02db3-2664-4d7e-80da-c52c5a34c635)

## 🛠 기술 스택

### 개발
<p>
    <img src="https://img.shields.io/badge/java%2017-007396?style=for-the-badge&logo=java&logoColor=white">
    <img src="https://img.shields.io/badge/spring%20boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
    <img src="https://img.shields.io/badge/jpa-F7DF1E?style=for-the-badge">
    <img src="https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
    <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
    <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
    <img src="https://img.shields.io/badge/querydsl-333333?style=for-the-badge">
</p>

### 인프라
<p>
    <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white">
    <img src="https://img.shields.io/badge/github%20actions-2088FF?style=for-the-badge&logo=github&logoColor=white">
    <img src="https://img.shields.io/badge/spring%20actuator-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
</p>

### 테스트
<p>
    <img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white">
    <img src="https://img.shields.io/badge/mockito-8D6E63?style=for-the-badge&logo=mockito&logoColor=white">
</p>



## ✏️ 문서/협업

<p>
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white">
<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
<img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
<img src="https://img.shields.io/badge/spring rest docs-6DB33F?style=for-the-badge">
</p>

## 🗺️ ERD
![gogoring_erd](https://github.com/GoGo-Ring/dongoorami-backend/assets/77786996/cb2ab04d-2420-4ec2-9c4b-33aa736f08bd)

## 🤙 Convention

### Branch 전략
- develop : Merge 전용 브랜치
- 이슈에 대한 브랜치 생성(Tag Name/[이슈번호]-이슈요약)


### Commit Convention

| Tag Name | Description                                                    |
|----------|----------------------------------------------------------------|
| feat     | 새로운 기능을 추가                                                     |
| refactor | 프로덕션 코드 리팩토링                                                   |
| docs     | 문서 수정                                                          |
| test     | 테스트 코드, 리펙토링 테스트 코드 추가, Production Code(실제로 사용하는 코드) 변경 없음     |
| chore    | 빌드 업무 수정, 패키지 매니저 수정, 패키지 관리자 구성 등 업데이트, Production Code 변경 없음 |
| style    | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우       
