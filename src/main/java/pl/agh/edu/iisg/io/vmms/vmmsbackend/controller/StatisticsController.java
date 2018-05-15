package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.statistics.StatsDataPointDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.statistics.StatsDataPointsDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.StatisticsService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/stats")
public class StatisticsController {

    private static final String HOURLY_USAGE_ENDPOINT = "/hourly";
    private static final String WEEKDAYS_HOURLY_USAGE_ENDPOINT = "/weekdays";
    private static final String MONTHS_HOURLY_USAGE_ENDPOINT = "/months";

    private StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @RequestMapping(path = HOURLY_USAGE_ENDPOINT, method = RequestMethod.GET)
    public List<StatsDataPointDto> getHoursUsageInInterval(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        to = addOneDay(to);
        Map<VMPool, Double> usageMap =
                statisticsService.getHourlyUsageInIntervalByVMPool(from, to);
        return usageMap
                .entrySet()
                .stream()
                .map(entry -> new StatsDataPointDto(
                        entry.getKey().getDisplayName(),
                        entry.getValue()
                        )
                )
                .collect(Collectors.toList());
    }

    @RequestMapping(path = WEEKDAYS_HOURLY_USAGE_ENDPOINT, method = RequestMethod.GET)
    public List<StatsDataPointsDto> getWeekdaysHourlyUsageInInterval(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        to = addOneDay(to);
        Map<VMPool, SortedMap<DayOfWeek, Double>> usageMap =
                statisticsService.getHourlyUsageInIntervalByVMPoolAndDayOfWeek(from, to);
        return usageMap
                .entrySet()
                .stream()
                .map(entry -> new StatsDataPointsDto(
                                entry.getKey().getDisplayName(),
                                new ArrayList<>(entry.getValue().values())
                        )
                )
                .collect(Collectors.toList());
    }

    @RequestMapping(path = MONTHS_HOURLY_USAGE_ENDPOINT, method = RequestMethod.GET)
    public List<StatsDataPointsDto> getMonthsHourlyUsageInInterval(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        to = addOneDay(to);
        Map<VMPool, SortedMap<Month, Double>> usageMap =
                statisticsService.getHourlyUsageInIntervalByVMPoolAndMonth(from, to);
        return usageMap
                .entrySet()
                .stream()
                .map(entry -> new StatsDataPointsDto(
                       entry.getKey().getDisplayName(),
                       new ArrayList<>(entry.getValue().values())
                        )
                )
                .collect(Collectors.toList());
    }

    private Date addOneDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
}
