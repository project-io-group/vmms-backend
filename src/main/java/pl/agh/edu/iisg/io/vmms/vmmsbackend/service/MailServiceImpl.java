package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import com.sendgrid.*;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.MailSendingFailureException;

import java.io.IOException;

/**
 * Created by Paweł Taborowski on 31.05.18.
 */
@Service
public class MailServiceImpl implements MailService{
    private final Email from;
    private final SendGrid sg;

    public MailServiceImpl(){
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

    @Override
    public void sendMail(String subject, String content, String toEmail, String toName)  throws MailSendingFailureException{
        Email to = new Email(toEmail, toName);
        sendMail_internal(subject, content, to);
    }

    @Override
    public void sendMail(String subject, String content, String toEmail)  throws MailSendingFailureException{
        Email to = new Email(toEmail);
        sendMail_internal(subject, content, to);
    }

    private void sendMail_internal(String subject, String content, Email to) throws MailSendingFailureException {
        Content contentObj = new Content("text/plain", content);

        Mail mail = new Mail(from, subject, to, contentObj);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            int status = response.getStatusCode();
            if (status < 200 || status > 299)
                throw new MailSendingFailureException("API returned HTTP status " + status);
        } catch (IOException ex) {
            throw new MailSendingFailureException(ex);
        }
    }
}
