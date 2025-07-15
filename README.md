# 맛집 추천 시스템 (Restaurant Recommendation System)

## 프로젝트 개요
카카오 맵 api를 기반으로 클릭한 위치 근처에 있는 식당을 추천하는 앱입니다.
AWS 배포: http://13.115.164.227:8080/

## 기술 스택

### Backend
Spring Boot

### External API
카카오 맵


## 환경설정 요구사항 : 카카오 API 키 설정
KAKAO_REST_API_KEY=YOUR_REST_API_KEY
KAKAO_JAVASCRIPT_KEY=YOUR_JAVASCRIPT_KEY

##개선사항
시간대나 사용자 데이터 기반의 선호를 이용한 가중치를 순위를 정하는데 사용하도록 만들기
파이썬 연동과의 속도를 최적화하기