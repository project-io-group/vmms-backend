package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.EmailConfigurationDTO;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.MailKeySubjectDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.InvalidEmailRequestException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.SendMailException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.MailService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.MailSubjectService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.VMAdminService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/email")
public class EmailController {
    private final MailSubjectService mailSubjectService;
    private final VMAdminService vmAdminService;
    private final MailService mailService;

    @Autowired
    EmailController(MailSubjectService mailSubjectService, VMAdminService vmAdminService, MailService mailService) {
        this.mailSubjectService = mailSubjectService;
        this.vmAdminService = vmAdminService;
        this.mailService = mailService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMail(@RequestParam("subject") String subjectKey,
                         @RequestParam("content") String rawContent)
            throws InvalidEmailRequestException, SendMailException {

        String subject = mailSubjectService.find(subjectKey)
                .orElseThrow(() ->
                        new InvalidEmailRequestException("Undefined subject: '" + subjectKey + "'"));

        mailService.sendToAllAdmins(subject, rawContent);
    }

    @RequestMapping(method = RequestMethod.POST, path = "configure/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public void importAdminsConfig(@RequestBody EmailConfigurationDTO json) {
        vmAdminService.drop();

        for (EmailConfigurationDTO.Admin adminDto : json.admins) {
            VMAdmin admin = new VMAdmin();
            admin.setName(adminDto.name);
            admin.setEMail(adminDto.mail);
            vmAdminService.save(admin);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "configure/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    public void importConfig(@RequestBody List<MailKeySubjectDto> keySubjectConfig) {
        mailSubjectService.drop();
        keySubjectConfig.forEach(ks -> mailSubjectService.save(ks.key, ks.subject));
    }
}
