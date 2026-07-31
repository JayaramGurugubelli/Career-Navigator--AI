package career_Navigator_parent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CareerNavigatorApplication {

	public static void main(String[] args) {

		SpringApplication.run(CareerNavigatorApplication.class, args);
	}
}