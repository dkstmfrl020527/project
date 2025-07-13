package org.koreait.survey.diabetes.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.propertis.PythonProperties;
import org.koreait.survey.diabetes.controllers.RequestDiabetesSurvey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(PythonProperties.class)
public class DiabetesSurveyPredictService {
    private final PythonProperties properties;
    private final WebApplicationContext ctx;
    private final ObjectMapper om;

    public List<Integer> process(List<List<Number>> items) {
        try {
            String data = om.writeValueAsString(items);

            // 맛집 추천과 동일한 방식으로 수정
            ProcessBuilder builder = new ProcessBuilder(
                    "src/main/resources/python/.venv/Scripts/python.exe",  // 맛집과 동일
                    properties.getDiabetes() + "/predict.py",  // diabetes/predict.py
                    data
            );

            builder.redirectErrorStream(false);
            Process process = builder.start();

            // 결과 읽기
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = outputReader.readLine()) != null) {
                result.append(line);
            }

            // 에러 출력 읽기
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.out.println("Python 에러: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("🔍 Python 종료 코드: " + exitCode);

            if (exitCode == 0) {
                return om.readValue(result.toString(), new TypeReference<>() {});
            } else {
                System.err.println("❌ Python 스크립트 실행 실패");
                return List.of();
            }

        } catch (Exception e) {
            System.err.println("❌ Python 실행 오류: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * 설문 하나에 대한 당뇨병 설문 결과
     *
     * @param item
     * @return
     */
    public boolean isDiabetes(List<Number> item) {
        List<Integer> results = process(List.of(item));

        return !results.isEmpty() && results.getFirst() == 1;
    }

    public boolean isDiabetes(RequestDiabetesSurvey form) {
        List<Number> item = new ArrayList<>();
        item.add(form.getGender().getNum());
        item.add(form.getAge());
        item.add(form.isHypertension() ? 1 : 0);
        item.add(form.isHeartDisease() ? 1 : 0);
        item.add(form.getSmokingHistory().getNum());

        // BMI 지수 계산
        double bmi = getBmi(form.getHeight(), form.getWeight());
        item.add(bmi);

        item.add(form.getHbA1c()); // 당화혈색소 수치
        item.add(form.getBloodGlucoseLevel()); // 혈당 수치

        return isDiabetes(item);
    }

    // BMI 지수 계산
    public double getBmi(double height, double weight) {
        height = height / 100.0;

       return Math.round((weight / Math.pow(height, 2.0)) * 100.0) / 100.0;
    }
}
