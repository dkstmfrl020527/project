import sys
import json
import numpy as np
import warnings
warnings.filterwarnings('ignore')

def rank_restaurants_knn(user_lat, user_lon, restaurants_data):
    """실시간 KNN 랭킹"""
    
    if len(restaurants_data) <= 1:
        return list(range(len(restaurants_data)))
    
    print(f"🤖 KNN 분석 시작: {len(restaurants_data)}개 식당", file=sys.stderr)
    
    # 각 식당에 대해 점수 계산
    scores = []
    for i, restaurant in enumerate(restaurants_data):
        lat = float(restaurant['lat'])
        lon = float(restaurant['lon'])
        category = restaurant.get('category', '').lower()
        
        # 1. 거리 점수 (가까울수록 높음)
        distance = ((lat - user_lat) ** 2 + (lon - user_lon) ** 2) ** 0.5
        distance_score = 1.0 / (1.0 + distance * 1000)  # 거리를 미터로 변환
        
        # 2. 카테고리 가중치
        category_weights = {
            '한식': 1.3,
            '일식': 1.2,
            '중식': 1.1,
            '양식': 1.0,
            '피자': 0.95,
            '카페': 0.8,
            '치킨': 0.9,
            '분식': 1.0
        }
        
        category_score = 1.0
        for key, weight in category_weights.items():
            if key in category:
                category_score = weight
                break
        
        # 3. 최종 점수
        final_score = distance_score * category_score
        scores.append((i, final_score))
    
    # 점수 순으로 정렬 (높은 점수부터)
    scores.sort(key=lambda x: x[1], reverse=True)
    
    # 인덱스만 반환
    result = [idx for idx, score in scores]
    print(f"✅ KNN 랭킹 완료: {result[:5]}...", file=sys.stderr)
    
    return result

def main():
    try:
        # 디버깅: 받은 인자들 출력
        
        # 인자 개수 체크 (정확히 4개여야 함: script명, lat, lon, json)
        if len(sys.argv) != 4:
            return
        
        user_lat = float(sys.argv[1])
        user_lon = float(sys.argv[2])
        restaurants_json = sys.argv[3]
        
        restaurants_data = json.loads(restaurants_json)
        
        if len(restaurants_data) == 0:
            print("[]")
            return
        
        # KNN 랭킹 실행
        ranked_indices = rank_restaurants_knn(user_lat, user_lon, restaurants_data)
        
        # 결과 출력
        print(json.dumps(ranked_indices))
        
    except Exception as e:
        print(f"❌ 오류 발생: {str(e)}", file=sys.stderr)
        # 오류 시 원본 순서 반환
        try:
            if len(sys.argv) >= 4:
                restaurants_data = json.loads(sys.argv[3])
                print(json.dumps(list(range(len(restaurants_data)))))
            else:
                print("[]")
        except:
            print("[]")

if __name__ == "__main__":
    main()