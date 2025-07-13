package org.koreait.map.controller;


import lombok.RequiredArgsConstructor;
import org.koreait.global.propertis.KakaoProperties;
import org.koreait.map.service.RestaurantInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.koreait.map.entity.Restaurant;


@Controller
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantInfoService restaurantService;
    private final KakaoProperties kakaoProperties;

    // 메인 페이지 - 리다이렉트 방식
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("kakaoJsKey", kakaoProperties.getJavascript().getKey());
        return "index";  // templates/index.html을 찾음
    }

    // 추천 API
    @ResponseBody
    @GetMapping("/api/recommend")
    public List<Restaurant> recommend(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1000") int radius,
            Model model) {

        String jsKey = kakaoProperties.getJavascript().getKey();  // ← 구조에 맞게 수정
        System.out.println("🔑 사용할 JavaScript 키: " + jsKey);

        model.addAttribute("kakaoJsKey", jsKey);

        return restaurantService.getNearbyRestaurants(lat, lon, radius);
    }
}
