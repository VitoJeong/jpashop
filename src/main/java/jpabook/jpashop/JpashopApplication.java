package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class JpashopApplication {

	public static void main(String[] args) {

		Hello hello = new Hello();
		hello.setData("hello");
		String data = hello.getData();

		log.info(data);

		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}
	// Hibernate5Module 기본설정 : 초기화 된 프록시 객체만 노출
	// FORCE_LAZY_LOADING 옵션 : 양방향 연관관계를 계속 로딩하게 됨 한쪽에 @JsonIgnore 설정해줘야함
	// -> Hibernate5Module 모듈을 사용하기 보다는 DTO로 변환해서 반환하는것이 좋은 방법!!

}
