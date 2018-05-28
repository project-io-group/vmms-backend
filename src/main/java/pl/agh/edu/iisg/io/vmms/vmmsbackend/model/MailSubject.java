package pl.agh.edu.iisg.io.vmms.vmmsbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
public class MailSubject {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String subjectKey;

    private String subject;
}
