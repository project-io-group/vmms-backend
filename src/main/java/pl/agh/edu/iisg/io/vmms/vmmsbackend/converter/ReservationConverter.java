package pl.agh.edu.iisg.io.vmms.vmmsbackend.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user.UserDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation.ReservationDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationConverter {

    private ModelMapper modelMapper;

    @Autowired
    public ReservationConverter(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = modelMapper.map(reservation, ReservationDto.class);

        List<ReservationPeriod> periods = reservation.getPeriods();
        List<Date> dates = periods
                .stream()
                .map(this::convertPeriodToDay)
                .collect(Collectors.toList());
        dto.setDates(dates);

        if(!periods.isEmpty()) {
            dto.setStartTime(periods.get(0).getStartDate());
            dto.setEndTime(periods.get(0).getEndDate());
        }

        dto.setOwner(modelMapper.map(reservation.getOwner(), UserDto.class));
        return dto;
    }

    public Date convertPeriodToDay(ReservationPeriod period){

        Calendar cal = Calendar.getInstance();
        cal.setTime(period.getStartDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
