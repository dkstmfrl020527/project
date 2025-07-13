package org.koreait.map.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.koreait.global.propertis.KakaoProperties;
import org.koreait.map.entity.KakaoApiResponse;
import org.koreait.map.entity.KakaoPlace;
import org.koreait.map.entity.Restaurant;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantInfoService {

    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;
    private final ObjectMapper objectMapper;

    /**
     * KNN 기반 맛집 추천 (DB 없이)
     */
    public List<Restaurant> getNearbyRestaurants(double lat, double lon, int radius) {
        try {
            System.out.println("🔍 KNN 맛집 추천 시작 - 위치: " + lat + ", " + lon);

            // 1️⃣ 카카오 API로 실시간 식당 데이터 수집
            List<Restaurant> kakaoRestaurants = getKakaoRestaurants(lat, lon, radius);

            if (kakaoRestaurants.isEmpty()) {
                System.out.println("❌ 주변에 식당이 없습니다");
                return List.of();
            }

            System.out.println("✅ 카카오 API에서 " + kakaoRestaurants.size() + "개 식당 수집");

            // 2️⃣ Python KNN 알고리즘 적용
            List<Restaurant> knnRecommendations = applyKNNRanking(kakaoRestaurants, lat, lon);

            System.out.println("🤖 KNN 추천 완료: " + knnRecommendations.size() + "개 식당");

            return knnRecommendations;

        } catch (Exception e) {
            System.err.println("❌ KNN 추천 실패: " + e.getMessage());
            e.printStackTrace();

            // 실패 시 카카오 API 원본 결과 반환
            return getKakaoRestaurants(lat, lon, radius);
        }
    }

    /**
     * 카카오 API로 주변 식당 수집
     */
    private List<Restaurant> getKakaoRestaurants(double lat, double lon, int radius) {
        String apiKey = kakaoProperties.getRest().getApi().getKey();

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("❌ 카카오 REST API 키가 설정되지 않았습니다");
            return List.of();
        }

        String url = "https://dapi.kakao.com/v2/local/search/category.json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        String requestUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("category_group_code", "FD6")  // 음식점 카테고리
                .queryParam("x", lon)
                .queryParam("y", lat)
                .queryParam("radius", radius)
                .queryParam("size", 15)                    // 최대 15개
                .queryParam("sort", "distance")
                .toUriString();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoApiResponse> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    entity,
                    KakaoApiResponse.class
            );

            return response.getBody().getDocuments().stream()
                    .map(this::convertToRestaurant)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("❌ 카카오 API 호출 실패: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Python KNN 알고리즘으로 식당 순위 재조정
     */
    private List<Restaurant> applyKNNRanking(List<Restaurant> restaurants, double lat, double lon) {
        try {
            System.out.println("🤖 Python KNN 알고리즘 실행 중...");

            String restaurantsJson = objectMapper.writeValueAsString(restaurants);
            System.out.println("🔍 전송할 데이터: " + restaurantsJson); // 추가

            String escapedJson = restaurantsJson.replace("\"", "\\\"");

            ProcessBuilder pb = new ProcessBuilder(
                    "src/main/resources/python/.venv/Scripts/python.exe",
                    "src/main/resources/python/realtime_knn.py",
                    String.valueOf(lat),
                    String.valueOf(lon),
                    restaurantsJson  // ProcessBuilder가 자동으로 처리해줄 거예요
            );

            pb.redirectErrorStream(false);
            Process process = pb.start();

            // 표준 출력 읽기
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = outputReader.readLine()) != null) {
                result.append(line);
                System.out.println("Python 출력: " + line); // 추가
            }

            // 에러 출력 읽기 (중요!)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.out.println("Python 에러: " + line); // 추가
            }

            int exitCode = process.waitFor();
            System.out.println("🔍 Python 종료 코드: " + exitCode); // 추가

            if (exitCode != 0) {
                System.err.println("❌ Python 스크립트 실행 실패 (종료 코드: " + exitCode + ")");
                return restaurants;
            }


            // 나머지 코드는 동일...
        } catch (Exception e) {
            System.err.println("❌ Python KNN 실행 실패: " + e.getMessage());
            e.printStackTrace();
            return restaurants;
        }
        return null;
    }

    /**
     * 카카오 데이터를 Restaurant 객체로 변환
     */
    private Restaurant convertToRestaurant(KakaoPlace place) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(place.getId());
        restaurant.setName(place.getPlace_name());
        restaurant.setCategory(place.getCategory_name());
        restaurant.setAddress(place.getAddress_name());
        restaurant.setRoadAddress(place.getRoad_address_name());
        restaurant.setLat(Double.parseDouble(place.getY()));
        restaurant.setLon(Double.parseDouble(place.getX()));
        restaurant.setPhone(place.getPhone());
        restaurant.setDistance(place.getDistance());
        restaurant.setPlaceUrl(place.getPlace_url());
        return restaurant;
    }

    /**
     * 순수 카카오 API만 사용 (비교용)
     */
    public List<Restaurant> getKakaoOnlyRestaurants(double lat, double lon, int radius) {
        return getKakaoRestaurants(lat, lon, radius);
    }
}