package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationPeriodRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.VMPoolRepository;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final double TIME_TO_HOURS_FACTOR = 60.0 * 60 * 1000;

    private ReservationPeriodRepository reservationPeriodRepository;
    private VMPoolRepository vmPoolRepository;

    @Autowired
    public StatisticsServiceImpl(ReservationPeriodRepository reservationPeriodRepository,
                                 VMPoolRepository vmPoolRepository) {
        this.reservationPeriodRepository = reservationPeriodRepository;
        this.vmPoolRepository = vmPoolRepository;
    }

    @Override
    public Map<VMPool, Double> getHourlyUsageInIntervalByVMPool(Date from, Date to) {
        Date now = new Date();
        Map<VMPool, Double> usageMap = new HashMap<>();
        for(VMPool vmPool : vmPoolRepository.findAll()) {
            List<ReservationPeriod> periods = reservationPeriodRepository
                    .getAllPeriodsForVMPoolBetween(vmPool.getId(), now, from, to);
            double sum = 0;
            for(ReservationPeriod period : periods) {
                int machinesNumber = period.getReservation().getMachinesNumber();
                Date start = ( period.getStartDate().after(from) ) ? period.getStartDate() : from;
                Date end = ( period.getEndDate().before(to) ) ? period.getEndDate() : to;

                double hours = (end.getTime() - start.getTime()) / TIME_TO_HOURS_FACTOR;
                sum += hours * machinesNumber;
            }
            usageMap.put(vmPool, sum);
        }
        return usageMap;
    }

    @Override
    public Map<VMPool, SortedMap<DayOfWeek, Double>> getHourlyUsageInIntervalByVMPoolAndDayOfWeek(Date from, Date to) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        Map<VMPool, SortedMap<DayOfWeek, Double>> usageMap = new HashMap<>();
        for(VMPool vmPool : vmPoolRepository.findAll()) {
            List<ReservationPeriod> periods = reservationPeriodRepository
                    .getAllPeriodsForVMPoolBetween(vmPool.getId(), now, from, to);
            SortedMap <DayOfWeek, Double> usageInWeek = new TreeMap<>();
            for(DayOfWeek day: DayOfWeek.values()){
                usageInWeek.put(day, 0.0);
            }
            for(ReservationPeriod period : periods) {
                int machinesNumber = period.getReservation().getMachinesNumber();
                Date start = ( period.getStartDate().after(from) ) ? period.getStartDate() : from;
                Date end = ( period.getEndDate().before(to) ) ? period.getEndDate() : to;

                calendar.setTime(start);
                DayOfWeek dayOfWeek = DayOfWeek.of(calendar.get(Calendar.DAY_OF_WEEK)); // why calendar is 0 based grr

                double hours = (end.getTime() - start.getTime()) / TIME_TO_HOURS_FACTOR;
                usageInWeek.put(dayOfWeek,
                        usageInWeek.get(dayOfWeek) + hours * machinesNumber);
            }
            usageMap.put(vmPool, usageInWeek);
        }
        return usageMap;
    }

    @Override
    public Map<VMPool, SortedMap<Month, Double>> getHourlyUsageInIntervalByVMPoolAndMonth(Date from, Date to) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        Map<VMPool, SortedMap<Month, Double>> usageMap = new HashMap<>();
        for(VMPool vmPool : vmPoolRepository.findAll()) {
            List<ReservationPeriod> periods = reservationPeriodRepository
                    .getAllPeriodsForVMPoolBetween(vmPool.getId(), now, from, to);
            SortedMap <Month, Double> usageInMonth = new TreeMap<>();
            for(Month month : Month.values()){
                usageInMonth.put(month, 0.0);
            }
            for(ReservationPeriod period : periods) {
                int machinesNumber = period.getReservation().getMachinesNumber();
                Date start = ( period.getStartDate().after(from) ) ? period.getStartDate() : from;
                Date end = ( period.getEndDate().before(to) ) ? period.getEndDate() : to;

                calendar.setTime(start);
                Month month = Month.of(calendar.get(Calendar.MONTH)+1); // why calendar is 0 based grr

                double hours = (end.getTime() - start.getTime()) / TIME_TO_HOURS_FACTOR;
                usageInMonth.put(month,
                        usageInMonth.getOrDefault(month, 0.0) + hours * machinesNumber);
            }
            usageMap.put(vmPool, usageInMonth);
        }
        return usageMap;
    }
}
