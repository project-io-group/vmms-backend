package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ReservationDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ReservationResponseDto;
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
@RequestMapping("/reservations")
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

    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public List<ReservationDto> getAllReservations(){
        return reservationService.getReservations()
                .stream()
                .map(reservation -> convertToDto(reservation))
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/between", method = RequestMethod.GET)
    public List<ReservationDto> getReservationsBetweenDates(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date to){
        return reservationService
                .getReservationsBetweenDates(from, to)
                .stream()
                .map(reservation -> convertToDto(reservation))
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/single/create", method = RequestMethod.POST)
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

    @RequestMapping(path="/single/confirm", method = RequestMethod.PUT)
    public String confirmSingleReservation(
            @RequestParam("reservationId") Long reservationId){
        return reservationService.confirm(reservationId);
    }

    @RequestMapping(path="/cyclic/create", method = RequestMethod.POST)
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

    @RequestMapping(path="/cyclic/confirm", method = RequestMethod.PUT)
    public String confirmCyclicReservation(
            @RequestParam("reservationId") Long reservationId){
        return reservationService.confirm(reservationId);
    }

    private ReservationDto convertToDto(Reservation reservation){
        ReservationDto dto = modelMapper.map(reservation, ReservationDto.class);
        dto.setDates(reservation.getDates());
        return dto;
    }

    private ReservationResponseDto convertToDto(ReservationResponse reservationResponse){
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setReservationDto(convertToDto(reservationResponse.getReservation()));
        List<ReservationDto> collisions = reservationResponse
                .getCollisions()
                .stream()
                .map(r -> convertToDto(reservationService.firstByDate(r)))
                .collect(Collectors.toList());
        dto.setCollisions(collisions);
        return dto;
    }
}
