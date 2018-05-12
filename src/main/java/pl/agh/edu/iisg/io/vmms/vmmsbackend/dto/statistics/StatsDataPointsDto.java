package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatsDataPointsDto {

    String name;

    List<Double> data;
}
