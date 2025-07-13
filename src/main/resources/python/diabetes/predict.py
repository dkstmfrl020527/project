import sys
import json

def predict_diabetes(data):
    """
    임시 당뇨 예측 함수
    실제로는 확률적 경사 하강법을 사용해야 함
    """
    results = []
    
    for features in data:
        if len(features) < 8:
            results.append(0)  # 데이터 부족 시 정상
            continue
            
        gender, age, hypertension, heart_disease, smoking, bmi, hba1c, glucose = features
        
        # 간단한 규칙 기반 예측 (임시)
        risk_score = 0
        
        # 나이 위험도
        if age > 60:
            risk_score += 0.3
        elif age > 45:
            risk_score += 0.2
        
        # 고혈압
        if hypertension:
            risk_score += 0.2
        
        # 심장질환
        if heart_disease:
            risk_score += 0.2
        
        # 흡연
        if smoking in [1, 2]:  # 현재 흡연
            risk_score += 0.1
        
        # BMI
        if bmi > 30:
            risk_score += 0.2
        elif bmi > 25:
            risk_score += 0.1
        
        # 당화혈색소
        if hba1c > 6.5:
            risk_score += 0.3
        elif hba1c > 5.7:
            risk_score += 0.1
        
        # 혈당
        if glucose > 140:
            risk_score += 0.3
        elif glucose > 100:
            risk_score += 0.1
        
        # 최종 판정
        diabetes = 1 if risk_score > 0.5 else 0
        results.append(diabetes)
    
    return results

def main():
    try:
        if len(sys.argv) < 2:
            print("[]")
            return
        
        # 입력 데이터 파싱
        input_data = json.loads(sys.argv[1])
        
        # 예측 실행
        results = predict_diabetes(input_data)
        
        # 결과 출력
        print(json.dumps(results))
        
    except Exception as e:
        # 오류 시 안전한 기본값 반환
        try:
            input_data = json.loads(sys.argv[1])
            print(json.dumps([0] * len(input_data)))  # 모두 정상으로 반환
        except:
            print("[]")

if __name__ == "__main__":
    main()