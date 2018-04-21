package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ReservationDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ReservationResponseDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.ReservationExpiredException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationResponse;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.ReservationService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.UserService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.VMPoolService;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
public class ReservationController {

    private final ReservationService  reservationService;
    private final UserService userService;
    private final VMPoolService vmPoolService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 UserService userService, VMPoolService vmPoolService){
        this.reservationService = reservationService;
        this.userService = userService;
        this.vmPoolService = vmPoolService;
        // I know it should be injected but I don't know where to create the Beam
        this.modelMapper = new ModelMapper();
    }

    @RequestMapping(path = "/reservations/all", method = RequestMethod.GET)
    public List<ReservationDto> getAllReservations(){
        return reservationService.getConfirmedOrBeforeDeadlineToConfirmReservations()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/reservations/between", method = RequestMethod.GET)
    public List<ReservationDto> getReservationsBetweenDates(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date to){
        return reservationService
                .getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDates(from, to)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/reservations/details", method = RequestMethod.GET)
    public ReservationDto getReservationDetails(
            @RequestParam("reservationId") Long id) throws HttpException{
        Optional<Reservation> reservation = reservationService.find(id);
        if(reservation.isPresent()){
            return convertToDto(reservation.get());
        }
        else{
            throw new ReservationExpiredException();
        }
    }

    @RequestMapping(path="vm/{vmShortName}/between", method = RequestMethod.GET)
    public List<ReservationDto> getReservationsBetweenDatesForVMPool(
            @PathVariable("vmShortName") String vmPoolShortName,
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date to){
        return reservationService
                .getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDatesForVMPool(vmPoolShortName, from, to)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/reservations/single/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDto createReservation(
            @RequestParam("userId") Long userId,
            @RequestParam("vmPoolId") Long vmPoolId,
            @RequestParam("courseName") String courseName,
            @RequestParam("machinesCount") Integer machinesCount,
            @RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date date){
        User user = userService.find(userId);
        VMPool vmPool = vmPoolService.find(vmPoolId);
        ReservationResponse reservationResponse = reservationService
                .saveTemporarySingle(user, vmPool, courseName, machinesCount, date);
        return convertToDto(reservationResponse);
    }

    @RequestMapping(path="/reservations/single/confirm", method = RequestMethod.PUT)
    public ReservationDto confirmSingleReservation(
            @RequestParam("reservationId") Long reservationId) throws ReservationExpiredException {
        Optional<Reservation> reservation = reservationService.findIfNotExpired(reservationId);
        if(reservation.isPresent()) {
            reservationService.confirm(reservation.get());
            return convertToDto(reservation.get());
        }
        else{
            throw new ReservationExpiredException();
        }
    }

    @RequestMapping(path="/reservations/cyclic/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDto createRservation(
            @RequestParam("userId") Long userId,
            @RequestParam("vmPoolId") Long vmPoolId,
            @RequestParam("courseName") String courseName,
            @RequestParam("machinesCount") Integer machinesCount,
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date to,
            @RequestParam("interval") Integer interval) {
        User user = userService.find(userId);
        VMPool vmPool = vmPoolService.find(vmPoolId);
        ReservationResponse reservationResponse = reservationService
                .saveTemporaryCyclic(user, vmPool, courseName, machinesCount,
                from, to, interval);
        return convertToDto(reservationResponse);
    }

    @RequestMapping(path="/reservations/cyclic/confirm", method = RequestMethod.PUT)
    public ReservationDto confirmCyclicReservation(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("cancelledDates") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") List<Date> cancelledDates)
            throws ReservationExpiredException {
        Optional<Reservation> reservation = reservationService.findIfNotExpired(reservationId);
        if(reservation.isPresent()) {
            reservationService.confirm(reservation.get());
            return convertToDto(reservation.get());
        }
        else{
            throw new ReservationExpiredException();
        }
    }

    private ReservationDto convertToDto(Reservation reservation){
        ReservationDto dto = modelMapper.map(reservation, ReservationDto.class);
        dto.setDates(reservation.getDates());
        return dto;
    }

    private ReservationResponseDto convertToDto(ReservationResponse reservationResponse){
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setReservationMade(convertToDto(reservationResponse.getReservationMade()));
        List<ReservationDto> collisions =
                reservationResponse.getCollisionsWithDesired()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        dto.setCollisionsWithDesired(collisions);
        return dto;
    }
}
