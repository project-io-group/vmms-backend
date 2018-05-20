package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.task.ReservationCleanupTask;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class VmmsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VmmsBackendApplication.class, args);
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
