/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.jassoft.email;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import uk.co.jassoft.markets.exceptions.email.EmailSendException;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

/**
 *
 * @author Jonny
 */
public class EmailSenderService
{
    private static final Logger LOG = LoggerFactory.getLogger(EmailSenderService.class);

    @Autowired
    private VelocityEngine velocityEngine;
    
    private String template;
    private String htmlTemplate;
    private String fromAddress;

    public EmailSenderService(String template, String htmlTemplate, String fromAddress) {
        this.template = template;
        this.htmlTemplate = htmlTemplate;
        this.fromAddress = fromAddress;
    }

    public void send(String recipientAddress, String subject, Map emailContent) throws EmailSendException
    {
        try
        {
            if(System.getenv("API_SEND_EMAILS").equals("false")) {
                LOG.info("API_SEND_EMAILS is set to [{}]", System.getenv("API_SEND_EMAILS"));
                return;
            }

            // Construct an object to contain the recipient address.
            Destination destination = new Destination().withToAddresses(recipientAddress);

            // Create the subject and body of the message.
            Content subjectContent = new Content().withData(subject);
            
            Body body = new Body();
            
            if(template != null)
                body = body.withText(new Content().withData(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, "UTF-8", emailContent)));
            
            if(htmlTemplate != null)
                body = body.withHtml(new Content().withData(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, htmlTemplate, "UTF-8", emailContent)));

            // Create a message with the specified subject and body.
            Message message = new Message().withSubject(subjectContent).withBody(body);

            // Assemble the email.
            SendEmailRequest request = new SendEmailRequest().withSource(fromAddress).withDestination(destination).withMessage(message);
                       
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(new EnvironmentVariableCredentialsProvider());
            Region REGION = Region.getRegion(Regions.EU_WEST_1);
            client.setRegion(REGION);
       
            // Send the email.
            client.sendEmail(request);  
        }
        catch (Exception exception) 
        {
            throw new EmailSendException(exception.getMessage(), exception);
        }
    }
}
