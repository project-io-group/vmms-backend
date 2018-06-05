package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class StatsDataPointDto {

    String name;
    Double value;
}
