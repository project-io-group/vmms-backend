package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.task.ReservationCleanupTask;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class VmmsBackendApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        System.out.println("Spring boot application running in CET timezone :" + new Date());
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(VmmsBackendApplication.class);
        addDefaultProfile(app);
        app.run(args);
    }

    private static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties = new HashMap<>();
        defProperties.put("spring.profiles.default", "dev");
        app.setDefaultProperties(defProperties);
    }

    @Bean
    ReservationCleanupTask reservationCleanupTask() {
        return new ReservationCleanupTask();
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
