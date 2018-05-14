package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VmmsBackendApplicationTests {

    @Test
    public void contextLoads() {
    }

//    @Test
//    public void setDatesForCyclicReservation(){
//        CyclicReservation cyclicReservation = new CyclicReservation();
//        // I'm assuming that hour in the opening date of the reservation is the start time of a single reservation
//        Calendar cal = Calendar.getInstance();
//        cal.set(2018, Calendar.APRIL, 23, 14, 20, 00);
//        Date startDateInclusive = cal.getTime();
//        cyclicReservation.setDates(new Date().from(new Instant()));
//    }

}
