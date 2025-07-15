import sys
import json
import numpy as np
import warnings
warnings.filterwarnings('ignore')

def rank_restaurants_knn(user_lat, user_lon, restaurants_data):
    if len(restaurants_data) <= 1:
        return list(range(len(restaurants_data)))

    scores = []
    for i, restaurant in enumerate(restaurants_data):
        lat = float(restaurant['lat'])
        lon = float(restaurant['lon'])

        # 거리 점수만 계산
        distance = ((lat - user_lat) ** 2 + (lon - user_lon) ** 2) ** 0.5
        distance_score = 1.0 / (1.0 + distance * 1000)

        scores.append((i, distance_score))  # category_score 제거

    # 점수 순으로 정렬 (높은 점수부터)
    scores.sort(key=lambda x: x[1], reverse=True)

    return [idx for idx, score in scores]

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