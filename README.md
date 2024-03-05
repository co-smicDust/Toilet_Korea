![logo](https://github.com/co-smicDust/Toilet_Korea/blob/main/logo.PNG)
# 주변 화장실 정보 제공 안드로이드 어플리케이션

## 🔗INDEX
- 🚩[프로젝트 소개](#프로젝트-소개)
  1. [프로젝트 기능](#1.-프로젝트-기능)
  2. [사용 기술](#2.-사용-기술)
  3. [실행 화면](#3.-실행-화면)
     
- 🚨[구조 및 설계](#구조-및-설계)
  1. [시스템 구성도](#1.-시스템-구성도)
  2. [DB 설계](#2.-db-설계)
     
- 📝[후기](#후기)

---


## 🚩프로젝트 소개
Toilette은 대학교 졸업프로젝트로 개발한, 전국 화장실 정보를 제공하는 안드로이드 앱입니다. 주변 공중화장실을 검색하고 정보를 얻을 수 있을 뿐 아니라, 위급 상황 발생시 비상연락망을 통해 위험한 상황을 알리거나, 사이렌을 울려 적절히 대처할 수 있도록 돕습니다. 또한 자신이 찾는 화장실의 조건에 맞게 필터링하여 검색할 수도 있습니다.

> [프로젝트 발표 보러가기](https://www.youtube.com/watch?v=HyulpPMK2ts)
> [프로젝트 시연 영상 보러가기](https://www.youtube.com/watch?v=_1gSb-shSos)

- 진행 기간: 2022.03.08 ~ 2022.10.07


- 팀 구성 및 역할

  | 팀원 | 역할 |
  | ----- | -------|
  | 권소현 | Front, Review, Emergency System |
  | 이지영 | Front, Map, Crawling, Search |
  | 황보소영 | Firebase 실시간 데이터 베이스 생성 및 연동<br>소셜 로그인과 사용자 인증 토큰 관리<br>개인정보수정 기능<br>비상 사이렌 기능 |


---


### 1. 프로젝트 기능
- 로그인 / 회원가입
- Google Maps API와 공공데이터를 활용하여 주변 공중화장실 위치 및 자세한 정보 제공
- 공중화장실 이용 후기
- 필터링 검색
- 비상 연락망 설정 및 위급 상황시 전화 및 문자 기능


### 2. 사용 기술
#### 2-1. 백엔드
|주요 프레임워크 / 라이브러리</span>|Build Tool / DataBase / etc...|
|---------------------------|------------------------------|
|- Kotlin<br>- Android Studio<br>- Firebase Auth<br>- Google Auth|- Firebase<br>- Google Maps API<br>- 공공데이터포털 API|
#### 2-2. 프론트엔드
- Kotlin


### 3. 실행 화면
<details><summary>로그인</summary>
  <p>
    
  ![beforeLogin](https://github.com/co-smicDust/Toilet_Korea/blob/main/login.jpg)
  
  </p>
</details>

<details><summary>메인</summary>
  <p>


- 화장실 지도
  ![map](https://github.com/co-smicDust/Toilet_Korea/blob/main/map.jpg)


- 화장실 상세보기
  ![toilet1](https://github.com/co-smicDust/Toilet_Korea/blob/main/toilet1.jpg)
  ![toilet2](https://github.com/co-smicDust/Toilet_Korea/blob/main/toilet2.jpg)
  

- 화장실 리뷰
  ![review](https://github.com/co-smicDust/Toilet_Korea/blob/main/review.jpg)
</p>
</details>


<details><summary>메뉴</summary>
  <p>


- 사이드바
  ![sidebar](https://github.com/co-smicDust/Toilet_Korea/blob/main/sidebar.jpg)

  
- 개인 정보 수정
  ![personalInfo](https://github.com/co-smicDust/Toilet_Korea/blob/main/personalInfo.jpg)


- 비상 연락망 수정
  ![emergency](https://github.com/co-smicDust/Toilet_Korea/blob/main/emergency.jpg)


- 화장실 선택후 sos 버튼을 눌러 비상연락망 접근
  ![emergencyChoice](https://github.com/co-smicDust/Toilet_Korea/blob/main/emergencychoice.jpg)


- 비상연락 - 문자
  ![emergencyMessage](https://github.com/co-smicDust/Toilet_Korea/blob/main/emergencymessage.jpg)
  

- 비상연락 - 전화
  ![emergencyCall](https://github.com/co-smicDust/Toilet_Korea/blob/main/emergencycall.jpg)


- 상세 검색
  ![filterSearch](https://github.com/co-smicDust/Toilet_Korea/blob/main/filterSearch.jpg)
  

- 검색 결과
  ![searchList](https://github.com/co-smicDust/Toilet_Korea/blob/main/searchlist.jpg)
</p>
</details>


---


## 🚨구조 및 설계
### 1. 시스템 구성도
![system](https://github.com/co-smicDust/Toilet_Korea/blob/main/system.PNG)


### 2. DB 설계
![ERD](https://github.com/co-smicDust/Toilet_Korea/blob/main/toilet_korea.png)


---


## 📝후기

프로젝트 경험이 없었던 세 팀원이 모여 맨 땅에 헤딩 정신으로 개발해나간 어플리케이션입니다. 그렇기 때문에 프로젝트를 진행하며 가장 힘들었던 점은, 사용하는 모든 기술이 처음이라 제한된 시간내에 그 모든 것을 공부하고 프로젝트에 적용해야 했다는 것이었습니다. 덕분에 프론트는 투박하지만, 각 기능을 구현하는데에 필요한 Kotlin과 Android Studio, Firebase, 공공데이터, google maps 등에 대해 알아갈 수 있었습니다. 그 과정에서 무수한 오류를 만나고 그들을 해결해나가는 경험 또한 쌓을 수 있었습니다.

특히 기억에 남는 것은 팀원들이 실시간 데이터 베이스에 접근하지 못했던 문제입니다. 이 때문에 로그인이 막혀 리뷰 등 다른 기능을 테스트 할 수 없었는데 API 문서 등을 통해 팀원들의 SDK인증서 지문을 Firebase에 등록하지 않아 발생한 문제임을 파악했습니다. Firebase에서 프로젝트를 다시 생성함으로써 해당 문제를 해결하였고 로그인이 필요한 타 기능들을 테스트할 수 있었습니다.
