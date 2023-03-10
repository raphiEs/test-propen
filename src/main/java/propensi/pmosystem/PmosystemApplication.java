package propensi.pmosystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(scanBasePackages = "propensi.pmosystem")//(exclude = {DataSourceAutoConfiguration.class })
public class PmosystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PmosystemApplication.class, args);
	}

}
