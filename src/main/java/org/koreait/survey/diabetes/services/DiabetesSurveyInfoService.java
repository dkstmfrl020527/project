package org.koreait.survey.diabetes.services;

import lombok.RequiredArgsConstructor;
import org.koreait.global.constants.Gender;
import org.koreait.survey.diabetes.constants.SmokingHistory;
import org.koreait.survey.diabetes.entities.DiabetesSurvey;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Lazy
@Service
@RequiredArgsConstructor
public class DiabetesSurveyInfoService {

    // 임시 메모리 저장소 (DB 대신)
    private final Map<Long, DiabetesSurvey> temporaryStorage = new ConcurrentHashMap<>();

    /**
     * 설문지 한개 조회 (메모리에서)
     */
    public DiabetesSurvey get(Long seq) {
        System.out.println("🔍 설문 결과 조회 시도 (메모리): " + seq);

        // 메모리에서 조회
        DiabetesSurvey item = temporaryStorage.get(seq);

        if (item != null) {
            System.out.println("✅ 메모리에서 설문 결과 조회 성공");
            return item;
        }

        // 없으면 기본값 생성
        System.out.println("❌ 설문 결과를 찾을 수 없음, 기본값 생성: " + seq);
        return createDefaultSurvey(seq);
    }

    /**
     * 메모리에 저장 (DB save 대신)
     */
    public DiabetesSurvey save(DiabetesSurvey survey) {
        if (survey.getSeq() == null) {
            survey.setSeq(System.currentTimeMillis());
        }

        temporaryStorage.put(survey.getSeq(), survey);
        System.out.println("✅ 메모리에 설문 저장 완료 (ID: " + survey.getSeq() + ")");

        return survey;
    }

    /**
     * 전체 목록 조회 (메모리에서)
     */
    public List<DiabetesSurvey> getList() {
        System.out.println("✅ " + temporaryStorage.size() + "개 설문 결과 조회 (메모리)");
        return temporaryStorage.values().stream().toList();
    }

    /**
     * 기본 설문 결과 생성
     */
    private DiabetesSurvey createDefaultSurvey(Long seq) {
        DiabetesSurvey defaultItem = new DiabetesSurvey();
        defaultItem.setSeq(seq);
        defaultItem.setDiabetes(false);
        defaultItem.setAge(30);
        defaultItem.setGender(Gender.FEMALE);
        defaultItem.setBmi(22.5);
        defaultItem.setHeight(165.0);
        defaultItem.setWeight(60.0);
        defaultItem.setHypertension(false);
        defaultItem.setHeartDisease(false);
        defaultItem.setSmokingHistory(SmokingHistory.NEVER);
        defaultItem.setHbA1c(5.5);
        defaultItem.setBloodGlucoseLevel(90.0);

        // 메모리에도 저장
        temporaryStorage.put(seq, defaultItem);

        return defaultItem;
    }

    /**
     * 최근 설문 결과 조회
     */
    public DiabetesSurvey getLatest() {
        if (temporaryStorage.isEmpty()) {
            return null;
        }

        return temporaryStorage.values().stream()
                .max((a, b) -> Long.compare(a.getSeq(), b.getSeq()))
                .orElse(null);
    }
}