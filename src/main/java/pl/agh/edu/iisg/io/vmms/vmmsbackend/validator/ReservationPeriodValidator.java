package pl.agh.edu.iisg.io.vmms.vmmsbackend.validator;

import org.springframework.beans.factory.annotation.Autowired;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationPeriodRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationPeriodValidator
        implements ConstraintValidator<ValidReservationPeriod, ReservationPeriod> {


    @Autowired
    private ReservationPeriodRepository reservationPeriodRepository;

    @Override
    public boolean isValid(
            ReservationPeriod reservationPeriod, ConstraintValidatorContext context) {

        if (reservationPeriod == null) {
            return true;
        }

        if (!(reservationPeriod instanceof ReservationPeriod)) {
            throw new IllegalArgumentException("Illegal method signature, "
                    + "expected parameter of type ReservationPeriod.");
        }

        if (reservationPeriod.getStartDate() == null
                || reservationPeriod.getEndDate() == null
                || reservationPeriod.getReservation() == null) {
            return false;
        }
        Date now = new Date();
        if(reservationPeriod.getStartDate().before(now)
                || reservationPeriod.getEndDate().before(now))
            return false;

        Reservation reservation = reservationPeriod.getReservation();
        List<ReservationPeriod> periodsColliding = reservationPeriodRepository
                .getAllPeriodsForVMPoolBetween(reservation.getPool(),
                        now,
                        reservationPeriod.getStartDate(),
                        reservationPeriod.getEndDate());

        Integer maxNumberReserved = getMaxMachinesCountInOneTime(periodsColliding);

        return maxNumberReserved + reservation.getMachinesNumber()
                <= reservation.getPool().getMaximumCount();
    }


    private Integer getMaxMachinesCountInOneTime(List<ReservationPeriod> periods){

        if (periods.isEmpty()){
            return 0;
        }

        List<ReservationPeriod> sortedByStartDate = periods
                .stream()
                .sorted((x, y) -> {
                    if(x.getStartDate().before(y.getStartDate()))
                        return 1;
                    return -1;
                } )
                .collect(Collectors.toList());

        List<ReservationPeriod> sortedByEndDate = periods
                .stream()
                .sorted((x, y) -> {
                    if(x.getEndDate().before(y.getStartDate()))
                        return 1;
                    return -1;
                } )
                .collect(Collectors.toList());

        Date currentDate;
        int currentMachinesNumber = 0;
        int max = 0;
        int j = 0;
        for(int i=0; i<periods.size(); i++){
            currentDate = sortedByStartDate.get(i).getStartDate();
            currentMachinesNumber += sortedByStartDate.get(i).getReservation().getMachinesNumber();

            while(!sortedByEndDate.get(j).getEndDate().after(currentDate)){
                currentMachinesNumber -= sortedByEndDate.get(j).getReservation().getMachinesNumber();
                j++;
            }
        }
        return max;
    }
}
