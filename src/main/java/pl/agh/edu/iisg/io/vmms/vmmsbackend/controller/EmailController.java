package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.sendgrid.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.InvalidEmailConfigurationException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.InvalidEmailRequestException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.MailSendingFailureException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.MailSubject;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.EmailConfigurationDTO;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.MailSubjectService;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.VMAdminService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/email")
public class EmailController {
    private final Email from;
    private final SendGrid sg;

    private final MailSubjectService mailSubjectService;
    private final VMAdminService vmAdminService;

    @Autowired
    EmailController(MailSubjectService mailSubjectService, VMAdminService vmAdminService){
        this.mailSubjectService = mailSubjectService;
        this.vmAdminService = vmAdminService;

        from = new Email("complaint@vmms.ki.agh.edu.pl");
        String apiKey;
        try {
            apiKey = System.getenv("SENDGRID_API_KEY");
            if (apiKey.equals("")) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e){
            throw new RuntimeException("Failed to access SendGrid API key! " +
                    "Please set SENDGRID_API_KEY environment variable");
        }
        sg = new SendGrid(apiKey);
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

        Email to = new Email(mailSubject.getAdmin().getEMail());

        Content content = new Content("text/plain", rawContent);
        Mail mail = new Mail(from, subject, to, content);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw new MailSendingFailureException(ex);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "configure")
    @ResponseStatus(HttpStatus.CREATED)
    public void importConfig(@RequestBody EmailConfigurationDTO json) throws InvalidEmailConfigurationException {
        mailSubjectService.drop();
        vmAdminService.drop();

        try {
            Map<String, VMAdmin> admins = new HashMap<>();
            for (EmailConfigurationDTO.Admin adminDto : json.admins) {
                VMAdmin admin = new VMAdmin();
                admin.setName(adminDto.name);
                admin.setEMail(adminDto.mail);
                admins.put(adminDto.name, vmAdminService.save(admin));
            }
            for (EmailConfigurationDTO.Subject subjectDto : json.subjects) {
                MailSubject subject = new MailSubject();
                subject.setSubjectKey(subjectDto.key);
                subject.setSubject(subjectDto.subject);
                VMAdmin admin = admins.get(subjectDto.admin);
                if (admin == null)
                    throw new InvalidEmailConfigurationException("Subject assigned to non-existing admin " + subjectDto.admin);
                subject.setAdmin(admin);
                mailSubjectService.save(subject);
            }
        }catch (InvalidEmailConfigurationException e){
            mailSubjectService.drop();
            vmAdminService.drop();
            throw e;
        }
    }
}
