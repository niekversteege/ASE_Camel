package nl.han.dare2date.service.web;

import java.util.logging.Level;
import javax.jms.JMSException;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.ApplyRegistrationRequest;
import nl.han.dare2date.applyregistrationservice.ApplyRegistrationResponse;
import nl.han.dare2date.applyregistrationservice.Creditcard;
import nl.han.dare2date.applyregistrationservice.Registration;
import org.apache.log4j.Logger;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

@Endpoint
public class ApplyRegistrationServiceEndpoint {

    private static final String REG_SUCCESSFUL_TOPIC = "RegistrationConfirmationTopic";
    private Logger log = Logger.getLogger(getClass().getName());
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public ApplyRegistrationServiceEndpoint(Marshaller marshaller,
            Unmarshaller unmarshaller) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    @PayloadRoot(localPart = "ApplyRegistrationRequest", namespace = "http://www.han.nl/schemas/messages")
    public ApplyRegistrationResponse applyRegistration(ApplyRegistrationRequest req) {
        boolean success = false;
        log.debug("asdf");

        ApplyRegistrationResponse ret = new ApplyRegistrationResponse();
        Creditcard cc = req.getRegistration().getUser().getCard();
        try {
            //check if valid
            //Connection con = JMSUtil.getConnection();
            ValidateCreditcardService vccs = new ValidateCreditcardService();
            success = vccs.validate(cc);

        } catch (JMSException ex) {
            java.util.logging.Logger.getLogger(ApplyRegistrationServiceEndpoint.class.getName()).log(Level.SEVERE, "Stuk", ex);
        } catch (NamingException ex) {
            java.util.logging.Logger.getLogger(ApplyRegistrationServiceEndpoint.class.getName()).log(Level.SEVERE, "Stuk", ex);
        }




        //invalid -> ValidateCreditcardservice
        //valid -> ConfirmRegistrationService

        ret.setRegistration(req.getRegistration());
        ret.getRegistration().setSuccesFul(success);
        return ret;
    }

    private void notifyEveryone(Registration reg) {
        try {
            ConfirmRegistrationService confirm = new ConfirmRegistrationService(REG_SUCCESSFUL_TOPIC);
            confirm.confirm(reg);

        } catch (JMSException | NamingException ex) {
            log.error("", ex);
        }
    }
}
