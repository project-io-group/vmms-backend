package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

public interface StatisticsService {

    Map<VMPool, Double> getHourlyUsageInIntervalByVMPool(Date from, Date to, Boolean includeDisabled);

    Map<VMPool, SortedMap<DayOfWeek, Double>>
    getHourlyUsageInIntervalByVMPoolAndDayOfWeek(Date from, Date to, Boolean includeDisabled);

    Map<VMPool, SortedMap<Month, Double>>
    getHourlyUsageInIntervalByVMPoolAndMonth(Date from, Date to, Boolean includeDisabled);
}
