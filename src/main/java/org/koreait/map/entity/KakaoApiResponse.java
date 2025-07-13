package org.koreait.map.entity;

import lombok.Data;
import java.util.List;

@Data
public class KakaoApiResponse {
    private List<KakaoPlace> documents;
    private Meta meta;

    @Data
    public static class Meta {
        private int total_count;
        private int pageable_count;
        private boolean is_end;
    }
}
