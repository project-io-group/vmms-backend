package pl.agh.edu.iisg.io.vmms.vmmsbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class MailSubject {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String subjectKey;

    private String subject;
}
