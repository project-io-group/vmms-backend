package pl.agh.edu.iisg.io.vmms.vmmsbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
public class MailKeySubject {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String key;

    private String subject;
}
