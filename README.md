# dongoorami-backend

> 동구라미🙆, 함께 공연을 즐길 동행인을 찾는 이들을 위한 특별한 서비스

## 🔗 API 문서 및 서비스 링크

- [RestDocs](https://www.dongoorami.shop:8080/docs/index.html)
- [동구라미 서비스](https://dongoorami.netlify.app/)

## 🌟 개발 포인트

- CI/CD 구축
- 커서 기반 무한 스크롤
- 필터링 QueryDsl 적극 활용
- 공연 Open API
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
