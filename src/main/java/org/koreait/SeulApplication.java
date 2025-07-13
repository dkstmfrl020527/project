package org.koreait;

import org.koreait.global.propertis.KakaoProperties;
import org.koreait.global.propertis.PythonProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
@EnableConfigurationProperties({PythonProperties.class, KakaoProperties.class})// ← 이 부분 추가!
public class SeulApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeulApplication.class, args);
	}
}