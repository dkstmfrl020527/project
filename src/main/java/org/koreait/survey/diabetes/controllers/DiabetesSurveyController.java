package org.koreait.survey.diabetes.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.constants.Gender;
import org.koreait.global.utils.Utils;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.koreait.survey.diabetes.services.DiabetesSurveyInfoService;
import org.koreait.survey.diabetes.services.DiabetesSurveyService;
import org.koreait.survey.diabetes.validators.DiabetesSurveyValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/survey/diabetes")
@SessionAttributes("requestDiabetesSurvey")
public class DiabetesSurveyController {

    // Utils 의존성 제거
    private final DiabetesSurveyValidator validator;
    private final DiabetesSurveyService surveyService;
    private final DiabetesSurveyInfoService infoService;

    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("survey/diabetes/style");
    }

    @ModelAttribute("requestDiabetesSurvey")
    public RequestDiabetesSurvey requestDiabetesSurvey() {
        RequestDiabetesSurvey form = new RequestDiabetesSurvey();
        form.setGender(Gender.FEMALE);
        form.setSmokingHistory(SmokingHistory.CURRENT);
        return form;
    }

    @ModelAttribute("genders")
    public Gender[] genders() {
        return Gender.values();
    }

    @ModelAttribute("smokingHistories")
    public SmokingHistory[] smokingHistories() {
        return SmokingHistory.values();
    }

    @GetMapping({"", "/step1"})
    public String step1(@ModelAttribute RequestDiabetesSurvey form, Model model) {
        commonProcess("step", model);
        return "diabetes/step1";  // Utils 제거
    }

    @PostMapping("/step2")
    public String step2(@Valid RequestDiabetesSurvey form, Errors errors, Model model) {
        form.setMode("step2");  // mode 설정 추가
        commonProcess("step", model);
        validator.validate(form, errors);

        if (errors.hasErrors()) {
            return "diabetes/step1";  // Utils 제거
        }
        return "diabetes/step2";  // Utils 제거
    }

    /**
     * 설문 저장 및 결과 처리
     */
    @PostMapping("/process")
    public String process(@Valid RequestDiabetesSurvey form, Errors errors, Model model, SessionStatus status) {
        form.setMode("step2");  // mode 설정 추가
        commonProcess("step", model);
        validator.validate(form, errors);

        if (errors.hasErrors()) {
            return "diabetes/step2";  // Utils 제거
        }

        try {
            // 설문 결과 및 저장 처리
            DiabetesSurvey item = surveyService.process(form);

            // 처리 완료 후 세션값 초기화
            status.setComplete();
            model.addAttribute("requestDiabetesSurvey", requestDiabetesSurvey());

            // 올바른 redirect 경로
            return "redirect:/survey/diabetes/result/" + item.getSeq();

        } catch (Exception e) {
            System.err.println("❌ 설문 처리 중 오류: " + e.getMessage());
            e.printStackTrace();

            // 오류 시 임시 결과 생성
            DiabetesSurvey tempItem = new DiabetesSurvey();
            tempItem.setSeq(9999L);
            tempItem.setDiabetes(false);
            tempItem.setAge(form.getAge());
            tempItem.setGender(form.getGender());

            model.addAttribute("item", tempItem);
            model.addAttribute("errorMessage", "시스템 오류가 발생했습니다. 임시 결과를 표시합니다.");

            commonProcess("result", model);
            return "diabetes/result";  // Utils 제거
        }
    }

    /**
     * 설문 결과 보기
     */
    @GetMapping("/result/{seq}")
    public String result(@PathVariable("seq") Long seq, Model model) {
        commonProcess("result", model);

        try {
            // 실제 데이터 조회 시도
            DiabetesSurvey item = infoService.get(seq);
            model.addAttribute("item", item);
            System.out.println("✅ 결과 조회 성공: " + item.getSeq());

        } catch (Exception e) {
            System.err.println("❌ 결과 조회 실패: " + e.getMessage());

            // 실패 시 기본 결과 생성
            DiabetesSurvey defaultItem = new DiabetesSurvey();
            defaultItem.setSeq(seq);
            defaultItem.setDiabetes(false);
            defaultItem.setAge(30);
            defaultItem.setGender(Gender.FEMALE);
            defaultItem.setBmi(22.5);

            model.addAttribute("item", defaultItem);
            model.addAttribute("errorMessage", "결과를 불러올 수 없습니다. 기본 결과를 표시합니다.");
        }

        return "diabetes/result";
    }

    /**
     * 테스트용 즉시 결과 페이지
     */
    @GetMapping("/test")
    public String test(Model model) {
        commonProcess("result", model);

        // 테스트용 샘플 데이터
        DiabetesSurvey testItem = new DiabetesSurvey();
        testItem.setSeq(1L);
        testItem.setDiabetes(false);
        testItem.setAge(35);
        testItem.setGender(Gender.MALE);
        testItem.setBmi(24.5);
        testItem.setHypertension(false);
        testItem.setHeartDisease(false);
        testItem.setSmokingHistory(SmokingHistory.NEVER);
        testItem.setHeight(175.0);
        testItem.setWeight(75.0);
        testItem.setHbA1c(5.2);
        testItem.setBloodGlucoseLevel(95.0);

        model.addAttribute("item", testItem);
        model.addAttribute("testMode", true);

        return "diabetes/result";
    }

    /**
     * 컨트롤러 공통 처리 (Utils 없이)
     */
    private void commonProcess(String mode, Model model) {
        String pageTitle = "";

        if ("step".equals(mode)) {
            pageTitle = "당뇨 고위험군 테스트";
        } else if ("result".equals(mode)) {
            pageTitle = "당뇨 고위험군 테스트 결과";
        }

        model.addAttribute("pageTitle", pageTitle);
    }
}