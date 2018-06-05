package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SubjectConfigurationDTO {
    public List<Subject> subjects;

    @NoArgsConstructor
    public static class Subject {
        public String key;
        public String subject;
    }
}


