package org.koreait.survey.diabetes.controllers;

import lombok.Data;
import org.koreait.global.constants.Gender;
import org.koreait.survey.diabetes.constants.SmokingHistory;

@Data
public class RequestDiabetesSurvey {
    private String mode; // step1, step2
    private Gender gender;
    private int age;
    private boolean hypertension; // 고혈압 여부
    private boolean heartDisease; // 심장질환 여부
    private SmokingHistory smokingHistory; // 흡연 여부
    private double height; // 키(cm)
    private double weight; // 몸무게(kg)
    private double hbA1c; // 당화혈색소 수치 (2~3개월 동안 평균 혈당 수치) %
    private double bloodGlucoseLevel; // 혈당 수치 (mg/dL)
}
