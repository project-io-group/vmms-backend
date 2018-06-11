package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.converter.ReservationConverter;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation.ReservationDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation.ReservationRequestDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation.ReservationResponseDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.ReservationDateNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.ReservationExpiredException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.ReservationNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.ApplicationUser;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.ReservationService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.UserService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.VMPoolService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private static final String CONFIRM_TMP_ENDPOINT = "/confirm";
    private static final String CANCEL_TMP_ENDPOINT = "/cancel";
    private static final String DELETE_DATES_FROM_RESERVATION_ENDPOINT = "/dates";

    private final ReservationService reservationService;
    private final UserService userService;
    private final VMPoolService vmPoolService;
    private final ReservationConverter reservationConverter;


    @Autowired
    public ReservationController(ReservationService reservationService,
                                 UserService userService, VMPoolService vmPoolService,
                                 ReservationConverter reservationConverter) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.vmPoolService = vmPoolService;
        this.reservationConverter = reservationConverter;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public ReservationDto getReservation(
            @RequestParam("reservationId") Long id) throws HttpException {
        Optional<Reservation> reservation = reservationService.find(id);
        if (reservation.isPresent()) {
            return reservationConverter.convertToDto(reservation.get());
        } else {
            throw new ReservationNotFoundException();
        }
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDto createReservation(
            @Valid @RequestBody ReservationRequestDto reservationRequest) {
        Reservation reservation = convertToReservation(reservationRequest);
        reservation = reservationService.saveTemporary(
                reservation,
                reservationRequest.getStartTime(),
                reservationRequest.getEndTime(),
                reservationRequest.getDates());

        List<Date> daysReserved = reservation
                .getPeriods()
                .stream()
                .map(reservationConverter::convertPeriodToDay)
                .collect(Collectors.toList());

        List<Date> daysRequested = reservationRequest.getDates();
        daysRequested.removeAll(daysReserved);
        return new ReservationResponseDto(reservation.getId(), daysRequested);
    }

    @RequestMapping(path = CONFIRM_TMP_ENDPOINT, method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void confirmReservation(
            @RequestBody Long reservationId) throws ReservationExpiredException, ReservationNotFoundException {
        Optional<Reservation> reservation = reservationService.find(reservationId);
        if (reservation.isPresent()) {
            if(reservation.get().isExpired()) {
                throw new ReservationExpiredException();
            }
            reservationService.confirm(reservation.get());
        } else {
            throw new ReservationNotFoundException();
        }
    }

    @RequestMapping(path = CANCEL_TMP_ENDPOINT, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void cancelReservation(
            @RequestParam Long reservationId) throws ReservationExpiredException, ReservationNotFoundException {
        Optional<Reservation> reservation = reservationService.find(reservationId);
        if (reservation.isPresent()) {
            if(reservation.get().isExpired()){
                throw new ReservationExpiredException();
            }
            reservationService.delete(reservation.get());
        } else {
            throw new ReservationNotFoundException();
        }
    }

    @RequestMapping(path = "", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteReservation(
            @RequestBody Long reservationId) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationService.findIfNotExpired(reservationId);
        if(reservation.isPresent()) {
            reservationService.delete(reservation.get());
        }
        else{
            throw new ReservationNotFoundException();
        }
    }

    @RequestMapping(path = DELETE_DATES_FROM_RESERVATION_ENDPOINT, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteDatesFromReservation (
            @RequestParam Long reservationId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") List<Date> cancelledDates)
            throws ReservationNotFoundException, ReservationDateNotFoundException
    {
        Optional<Reservation> reservation = reservationService.findIfNotExpired(reservationId);

        if(reservation.isPresent()) {
            Reservation extractedReservation = reservation.get();
            List<ReservationPeriod> processedPeriods = extractedReservation.getPeriods();
            List<Date> reservationDates = processedPeriods
                    .stream()
                    .map(reservationConverter:: convertPeriodToDay)
                    .collect(Collectors.toList());

            for (Date date: cancelledDates){
                if (!reservationDates.contains(date)) throw new ReservationDateNotFoundException();
            }
            for(ReservationPeriod period : processedPeriods){
                Date periodDay = reservationConverter.convertPeriodToDay(period);
                if(cancelledDates.contains(periodDay))
                    extractedReservation.removePeriod(period);
                    reservationService.deletePeriod(period);
            }
        }
        else{
            throw new ReservationNotFoundException();
        }
    }

    private Reservation convertToReservation(ReservationRequestDto request) {

        ApplicationUser user = userService.find(request.getUserEmail());
        VMPool vmPool = vmPoolService.find(request.getVmPoolId());

        Reservation reservation = new Reservation();
        reservation.setCourseName(request.getCourseName());
        reservation.setPool(vmPool);
        reservation.setMachinesNumber(request.getMachinesNumber());
        reservation.setOwner(user);
        return reservation;
    }
}
