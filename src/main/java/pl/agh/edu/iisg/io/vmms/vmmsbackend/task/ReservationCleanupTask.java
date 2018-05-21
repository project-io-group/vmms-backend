package pl.agh.edu.iisg.io.vmms.vmmsbackend.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.ReservationService;

public class ReservationCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(ReservationCleanupTask.class);

    @Autowired
    private ReservationService reservationService;

    @Scheduled(fixedDelayString = "PT30M")
    public void deleteExpiredOrEmptyReservations() {
        reservationService.deleteExpiredOrEmptyReservations();
        logger.info("Success");
    }

}
