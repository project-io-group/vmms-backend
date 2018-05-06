package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

@SpringBootApplication
public class VmmsBackendApplication {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    public static void main(String[] args) {
        SpringApplication.run(VmmsBackendApplication.class, args);
    }
}
