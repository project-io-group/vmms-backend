package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Data
@NoArgsConstructor
public class ReservationDto {

    private static final SimpleDateFormat dateFormat
            = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private Long id;

    private UserDto user;

    private String courseName;

    private VMPoolDto vmPool;

    private Integer quantity;

    private List<String> dates;

    public List<Date> getDatesConverted() throws java.text.ParseException {
        dateFormat.setTimeZone(TimeZone.getDefault());
        List<Date> datesConverted = new ArrayList<>();
        for(String date: dates)
            datesConverted.add(dateFormat.parse(date));
        return datesConverted;
    }

    public void setDates(List<Date> dates) {
        dateFormat.setTimeZone(TimeZone.getDefault());
        this.dates = new ArrayList<String>();
        for(Date date: dates)
            this.dates.add(dateFormat.format(date));
    }
}
