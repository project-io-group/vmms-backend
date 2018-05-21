package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EmailConfigurationDTO {
    public List<Admin> admins;
    public List<Subject> subjects;

    @NoArgsConstructor
    public static class Subject {
        public String key;
        public String subject;
        public String admin;
    }

    @NoArgsConstructor
    public static class Admin {
        public String name;
        public String mail;
    }
}


