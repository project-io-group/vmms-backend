package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.ReservationDateNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.ReservationExpiredException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.ReservationNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.ReservationService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.UserService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.VMPoolService;

import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
public class ReservationController {

    private static final String RESERVATIONS_ENDPOINT = "/reservations";
    private static final String RESERVATIONS_IN_PERIOD_ENDPOINT = "/reservations/between";
    private static final String RESERVATION_ENDPOINT = "/reservation";
    private static final String CONFIRM_TMP_ENDPOINT = "/reservation/confirm";
    private static final String CANCEL_TMP_ENDPOINT = "/reservation/cancel";
    private static final String DELETE_DATES_FROM_RESERVATION_ENDPOINT = "/reservation/dates";

    private final ReservationService reservationService;
    private final UserService userService;
    private final VMPoolService vmPoolService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 UserService userService, VMPoolService vmPoolService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.vmPoolService = vmPoolService;
        // I know it should be injected but I don't know where to create the Beam
        this.modelMapper = new ModelMapper();
    }

    @RequestMapping(path = RESERVATIONS_ENDPOINT, method = RequestMethod.GET)
    public List<ReservationDto> getReservationsForUser(
            @RequestParam("userId") Long userId) {
        return reservationService.getConfirmedOrBeforeDeadlineToConfirmReservations()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = RESERVATIONS_IN_PERIOD_ENDPOINT, method = RequestMethod.GET)
    public List<ReservationDto> getReservationsBetweenDatesForUser(
            @RequestParam("userId") Long userId,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date to) {
        return reservationService
                .getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDates(from, to)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "vm/{vmShortName}/between", method = RequestMethod.GET)
    public List<ReservationDto> getReservationsBetweenDatesForVMPool(
            @PathVariable("vmShortName") String vmPoolShortName,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date to) {
        return reservationService
                .getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDatesForVMPool(vmPoolShortName, from, to)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = RESERVATION_ENDPOINT, method = RequestMethod.GET)
    public ReservationDto getReservation(
            @RequestParam("reservationId") Long id) throws HttpException {
        Optional<Reservation> reservation = reservationService.find(id);
        if (reservation.isPresent()) {
            return convertToDto(reservation.get());
        } else {
            throw new ReservationExpiredException();
        }
    }

    @RequestMapping(path = RESERVATION_ENDPOINT, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Long createReservation(
            @RequestParam("reservationRequest")ReservationRequestDto reservationRequest) {
        Reservation reservation = convertToReservation(reservationRequest);
        return reservationService.saveTemporary(
                reservation,
                reservationRequest.getStartTime(),
                reservationRequest.getEndTime(),
                reservationRequest.getDates());
    }

    @RequestMapping(path = CONFIRM_TMP_ENDPOINT, method = RequestMethod.PUT)
    public String confirmReservation(
            @RequestParam("reservationId") Long reservationId) throws ReservationExpiredException {
        Optional<Reservation> reservation = reservationService.findIfNotExpired(reservationId);
        if (reservation.isPresent()) {
            reservationService.confirm(reservation.get());
            return "CONFIRMED";
        } else {
            throw new ReservationExpiredException();
        }
    }

    @RequestMapping(path = CANCEL_TMP_ENDPOINT, method = RequestMethod.DELETE)
    public String cancelReservation(
            @RequestParam("reservationId") Long reservationId) throws ReservationExpiredException {
        Optional<Reservation> reservation = reservationService.find(reservationId);
        if (reservation.isPresent()) {
            reservationService.delete(reservation.get());
            return "CANCELED";
        } else {
            throw new ReservationExpiredException();
        }
    }

    @RequestMapping(path = RESERVATION_ENDPOINT, method = RequestMethod.DELETE)
    public String deleteReservation(
            @RequestParam("reservationId") Long reservationId) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationService.findIfNotExpired(reservationId);
        if(reservation.isPresent()) {
            reservationService.delete(reservation.get());
            return "Done.";
        }
        else{
            throw new ReservationNotFoundException();
        }
    }

    @RequestMapping(path = DELETE_DATES_FROM_RESERVATION_ENDPOINT, method = RequestMethod.DELETE)
    public String deleteDatesFromReservation (
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("cancelledDates") @DateTimeFormat(pattern="yyyy-MM-dd") List<Date> cancelledDates)
            throws ReservationNotFoundException, ReservationDateNotFoundException
    {
        Optional<Reservation> reservation = reservationService.findIfNotExpired(reservationId);

        if(reservation.isPresent()) {
            Reservation extractedReservation = reservation.get();
            Set<ReservationPeriod> processedPeriods = extractedReservation.getPeriods();
            List<Date> reservationDates = processedPeriods
                    .stream()
                    .map(this:: convertPeriodToDay)
                    .collect(Collectors.toList());

            for (Date date: cancelledDates){
                if (!reservationDates.contains(date)) throw new ReservationDateNotFoundException();
            }
            for(ReservationPeriod period : processedPeriods){
                Date periodDay = convertPeriodToDay(period);
                if(cancelledDates.contains(periodDay))
                    processedPeriods.remove(period);
            }
        }
        else{
            throw new ReservationNotFoundException();
        }
        return "Done";
    }

    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = modelMapper.map(reservation, ReservationDto.class);

        //mock
        Set<ReservationPeriod> periods = reservation.getPeriods();
        List<Date> dates = periods
                .stream()
                .map(p -> p.getStartDate())
                .collect(Collectors.toList());
        dto.setDates(dates);

        dto.setOwner(modelMapper.map(reservation.getOwner(), UserDto.class));
        return dto;
    }

    private Reservation convertToReservation(ReservationRequestDto request) {

        User user = userService.find(request.getUserId());
        VMPool vmPool = vmPoolService.find(request.getVmPoolId());

        Reservation reservation = new Reservation();
        reservation.setCourseName(request.getCourseName());
        reservation.setPool(vmPool);
        reservation.setMachinesNumber(request.getMachinesNumber());
        reservation.setOwner(user);
        return reservation;
    }

    private Date convertPeriodToDay(ReservationPeriod period){

        Calendar cal = Calendar.getInstance();
        cal.setTime(period.getStartDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
