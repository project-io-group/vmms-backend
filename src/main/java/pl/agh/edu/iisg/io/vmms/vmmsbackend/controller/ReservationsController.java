package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.converter.ReservationConverter;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation.ReservationDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.ReservationService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
public class ReservationsController {

    private static final String RESERVATIONS_ENDPOINT = "/reservations";
    private static final String RESERVATIONS_IN_PERIOD_ENDPOINT = "/reservations/between";

    private final ReservationService reservationService;
    private final ReservationConverter reservationConverter;

    @Autowired
    public ReservationsController(ReservationService reservationService,
                                 ReservationConverter reservationConverter) {
        this.reservationService = reservationService;
        this.reservationConverter = reservationConverter;
    }

    @RequestMapping(path = RESERVATIONS_ENDPOINT, method = RequestMethod.GET)
    public List<ReservationDto> getReservationsForUser(
            @RequestParam("userId") Long userId) {
        return reservationService.getConfirmedOrBeforeDeadlineToConfirmReservations()
                .stream()
                .filter(r -> r.getOwner().getId().equals(userId))
                .map(reservationConverter::convertToDto)
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
                .filter(r -> r.getOwner().getId().equals(userId))
                .map(reservationConverter::convertToDto)
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
                .map(reservationConverter::convertToDto)
                .collect(Collectors.toList());
    }
}
