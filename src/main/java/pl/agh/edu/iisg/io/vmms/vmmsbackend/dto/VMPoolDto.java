package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class VMPoolDto {

    private Long id;

    private String shortName;

    private String displayName;

    private Integer maximumCount;

    private Boolean enabled;

    private String description;
}
