package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ReservationResponseDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.UserNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.CyclicReservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationResponse;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.SingleReservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.UserRepository;

import javax.xml.crypto.Data;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    ReservationRepository reservationRepository;

    @Autowired
    ReservationServiceImpl(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservations(){
        return reservationRepository
                .getByConfirmDateNotNullOrDeadlineToConfirmAfter(new Date());
    }

    @Override
    public List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDates(Date from, Date to) {
        return reservationRepository
                .getAllValidByDatesBetween(new Date(),from, to);
    }

    @Override
    public Optional<Reservation> find(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDatesForVMPool(String vmPoolShortName, Date from, Date to){
        return reservationRepository
                .getAllValidByDatesBetweenForVMPool(new Date(), from, to, vmPoolShortName);
    }

    @Override
    public ReservationResponse saveTemporarySingle(User user, VMPool vmPool, String courseName, Integer machinesCount, Date date) {
        Reservation reservation = new SingleReservation();
        reservation.setOwner(user);
        reservation.setPool(vmPool);
        reservation.setCourseName(courseName);
        reservation.setMachinesNumber(machinesCount);

        Date createDate = new Date();
        reservation.setCreateDate(createDate);
        reservation.setDeadlineToConfirmAccordingToDate(createDate);

        reservationRepository.save(reservation);

        List<Date> dates = new ArrayList<>();
        dates.add(date);
        reservation.setDates(dates);

        return saveTemporary(reservation);
    }

    @Override
    public ReservationResponse saveTemporaryCyclic(User user, VMPool vmPool, String courseName, Integer machinesCount, Date from, Date to, Integer interval) {
        Reservation reservation = new CyclicReservation();
        reservation.setOwner(user);
        reservation.setPool(vmPool);
        reservation.setCourseName(courseName);
        reservation.setMachinesNumber(machinesCount);

        Date createDate = new Date();
        reservation.setCreateDate(createDate);
        reservation.setDeadlineToConfirmAccordingToDate(createDate);

        reservationRepository.save(reservation);

        ((CyclicReservation) reservation).setDates(from, to, Period.of(0,0,interval));

        return saveTemporary(reservation);
    }

    private ReservationResponse saveTemporary(Reservation reservation){

        ReservationResponse response = new ReservationResponse();

        List<Reservation> collidingReservations = new ArrayList<>();

        for(Date date : reservation.getDates()){
            List<Reservation> collidingInDate = reservationRepository.findAllValidByPoolAndDate(
                    reservation.getCreateDate(),
                    reservation.getPool(),
                    date
                    );
            Integer sumOfMachinesReserved = 0;
            for(Reservation r: collidingInDate){
                sumOfMachinesReserved += r.getMachinesNumber();
            }
            if(collidingInDate.isEmpty() ||
                    (sumOfMachinesReserved + reservation.getMachinesNumber())
                            <= reservation.getPool().getMaximumCount()){
                reservation = reservationRepository.saveDateInReservation(reservation.getId(), date);
            }
            else{
                collidingReservations.addAll(collidingInDate);
            }
        }

        response.setCollisionsWithDesired(collidingReservations);
        response.setReservationMade(reservation);
        return response;
    }

    @Override
    public Optional<Reservation> findIfNotExpired(Long id) {
        return reservationRepository
                .getAllByIdAndConfirmDateNotNullOrDeadlineToConfirmBefore(id, new Date());
    }

    @Override
    public Reservation confirm(Reservation reservation){

        reservation.setConfirmDate(new Date());
        return reservationRepository.save(reservation);
    }
}
