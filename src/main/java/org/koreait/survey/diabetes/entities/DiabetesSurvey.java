package org.koreait.survey.diabetes.entities;

import lombok.Data;
import org.koreait.global.constants.Gender;
import org.koreait.global.entities.BaseEntity;
import org.koreait.survey.diabetes.constants.SmokingHistory;

@Data
public class DiabetesSurvey extends BaseEntity {
    private Long seq;
    private Long memberSeq;

    private Gender gender;
    private int age;
    private boolean hypertension;
    private boolean heartDisease;
    private SmokingHistory smokingHistory;
    private double height;
    private double weight;
    private double bmi;
    private double hbA1c;
    private double bloodGlucoseLevel;

    private boolean diabetes;
}
