package org.koreait.global.propertis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "python.path")
public class PythonProperties {

    private String base = "python";  // 기본값
    private String restaurant = "src/main/resources/python";  // 기본값
    private String diabetes = "src/main/resources/python/diabetes";  // 당뇨 전용 경로

    // 편의 메소드들
    public String getRestaurantScriptPath() {
        return restaurant + "/search.py";
    }

    public String getRealtimeKnnPath() {
        return restaurant + "/realtime_knn.py";
    }

    public String getDiabetesPredictPath() {
        return diabetes + "/predict.py";
    }

    public String getTrainPath() {
        return restaurant + "/train.py";
    }
}
