import os
import sys
import json
import pickle
import numpy as np
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import KNeighborsClassifier

base_path = os.path.dirname(os.path.realpath(__file__)) + "/"

if len(sys.argv) < 3:
    sys.exit(-1)

lat = float(sys.argv[1]) # 위도 
lon = float(sys.argv[2]) # 경도
num = int(sys.argv[3]) if len(sys.argv) > 3 else 10 # 조회할 식당 갯수

# 타겟 불러오기
with open(base_path + "target.pkl", "rb") as f:
    target = pickle.load(f)


# 표준점수 변환기 불러오기
with open(base_path + "scaler.pkl", "rb") as f:
    scaler = pickle.load(f)

# 모델 불러오기
with open(base_path + "model.pkl", "rb") as f:
    model = pickle.load(f)

# 인접한 식당 번호 조회 
current = scaler.transform(np.array([[lat, lon]]))

model.n_neighbors = num

_, indexes = model.kneighbors(current)
items = target[indexes][0].tolist()
print(json.dumps(items))