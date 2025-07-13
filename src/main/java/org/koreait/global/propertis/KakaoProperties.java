package org.koreait.global.propertis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Data
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    private Rest rest = new Rest();
    private Javascript javascript = new Javascript();

    @Data
    public static class Rest {
        private Api api = new Api();

        @Data
        public static class Api {
            private String key;
        }
    }

    @Data
    public static class Javascript {
        private String key;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}