package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import com.sendgrid.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.SendMailException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;

import java.io.IOException;


@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    private final Email from;
    private final SendGrid sg;
    private final VMAdminService vmAdminService;

    @Autowired
    public MailServiceImpl(VMAdminService vmAdminService) {

        this.vmAdminService = vmAdminService;

        from = new Email("vmms@ki.agh.edu.pl");
        String apiKey;
        try {
            apiKey = System.getenv("SENDGRID_API_KEY");
            if (apiKey.equals("")) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("Failed to access SendGrid API key! " +
                    "Please set SENDGRID_API_KEY environment variable");
        }
        sg = new SendGrid(apiKey);
    }

    @Override
    public void sendToAllAdmins(String subject, String content) throws SendMailException {
        for (VMAdmin admin : vmAdminService.getVmAdmins()) {
            send(subject, content, new Email(admin.getEMail(), admin.getName()));
        }
    }

    @Override
    public void send(String subject, String content, Email to) throws SendMailException {
        Content contentObj = new Content("text/plain", content);

        Mail mail = new Mail(from, subject, to, contentObj);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            int status = response.getStatusCode();
            if (status < 200 || status > 299) {
                throw new SendMailException("API returned HTTP status " + status);
            }
            logger.info("Sent mail '{}' to '{}'", subject, to.getEmail());
        } catch (IOException ex) {
            throw new SendMailException(ex);
        }
    }
}
