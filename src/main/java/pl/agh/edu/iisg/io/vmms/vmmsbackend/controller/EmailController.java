package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.SubjectConfigurationDTO;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.InvalidEmailRequestException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.MailSendingFailureException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.MailSubject;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.EmailConfigurationDTO;
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
    EmailController(MailSubjectService mailSubjectService, VMAdminService vmAdminService, MailService mailService){
        this.mailSubjectService = mailSubjectService;
        this.vmAdminService = vmAdminService;
        this.mailService = mailService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMail(@RequestParam("subject") String subjectKey,
                         @RequestParam("content") String rawContent)
            throws InvalidEmailRequestException, MailSendingFailureException {
        MailSubject mailSubject = mailSubjectService.find(subjectKey);
        if (mailSubject == null){
            throw new InvalidEmailRequestException("No such subject defined: '" + subjectKey + "'");
        }
        String subject = mailSubject.getSubject();

        List<VMAdmin> vmAdmins = vmAdminService.getVmAdmins();
        for(VMAdmin vmAdmin: vmAdmins){
            mailService.sendMail(subject, rawContent, vmAdmin.getEMail(), vmAdmin.getName());
        }
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
    public void importConfig(@RequestBody SubjectConfigurationDTO subjectConfiguration) {
        mailSubjectService.drop();

        for (SubjectConfigurationDTO.Subject subjectDto : subjectConfiguration.subjects) {
            MailSubject subject = new MailSubject();
            subject.setSubjectKey(subjectDto.key);
            subject.setSubject(subjectDto.subject);
            mailSubjectService.save(subject);
        }
    }
}
