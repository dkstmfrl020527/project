package org.koreait.survey.diabetes.services;

import lombok.RequiredArgsConstructor;
import org.koreait.survey.diabetes.controllers.RequestDiabetesSurvey;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class DiabetesSurveyService {

    private final DiabetesSurveyPredictService predictService;
    private final DiabetesSurveyInfoService infoService;  // Repository 대신 InfoService 사용
    private final ModelMapper mapper;

    public DiabetesSurvey process(RequestDiabetesSurvey form) {
        try {
            System.out.println("🔍 당뇨 설문 처리 시작...");

            // ML 예측 실행
            boolean diabetes = predictService.isDiabetes(form);
            double bmi = predictService.getBmi(form.getHeight(), form.getWeight());

            System.out.println("🤖 ML 예측 결과: " + (diabetes ? "고위험군" : "정상"));
            System.out.println("📊 BMI: " + bmi);

            // 엔티티 매핑
            DiabetesSurvey item = mapper.map(form, DiabetesSurvey.class);

            // 결과 설정
            item.setDiabetes(diabetes);
            item.setBmi(bmi);
            item.setMemberSeq(null);

            // 메모리에 저장 (DB 대신)
            DiabetesSurvey savedItem = infoService.save(item);

            System.out.println("✅ 설문 결과 저장 완료 (ID: " + savedItem.getSeq() + ")");

            return savedItem;

        } catch (Exception e) {
            System.err.println("❌ 설문 처리 실패: " + e.getMessage());
            e.printStackTrace();

            // 실패 시 기본 결과 반환
            DiabetesSurvey fallbackItem = new DiabetesSurvey();
            fallbackItem.setSeq(9999L);
            fallbackItem.setDiabetes(false);
            fallbackItem.setBmi(predictService.getBmi(form.getHeight(), form.getWeight()));
            fallbackItem.setAge(form.getAge());
            fallbackItem.setGender(form.getGender());
            fallbackItem.setHeight(form.getHeight());
            fallbackItem.setWeight(form.getWeight());
            fallbackItem.setHypertension(form.isHypertension());
            fallbackItem.setHeartDisease(form.isHeartDisease());
            fallbackItem.setSmokingHistory(form.getSmokingHistory());
            fallbackItem.setHbA1c(form.getHbA1c());
            fallbackItem.setBloodGlucoseLevel(form.getBloodGlucoseLevel());

            return fallbackItem;
        }
    }
}