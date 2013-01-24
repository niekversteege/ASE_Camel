/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;
import nl.han.dare2date.service.jms.util.JMSUtil;
import nl.han.dare2date.service.web.ValidateCreditcardService;
import org.apache.log4j.Logger;

/**
 *
 * @author kaaz
 */
public class ExternalCreditcardValidator {
    
    private final Logger log = Logger.getLogger(getClass().getName());
    
    public static void main(String[] args) {
        ExternalCreditcardValidator val = new ExternalCreditcardValidator();
        val.run();
    }
    
    private void run() {
        try {
            Connection conn = JMSUtil.getConnection();
            ValidateCreditcardService validateCreditcardService = new ValidateCreditcardService(conn);
        } catch (NamingException | JMSException ex) {
            log.error("Error creating connection and service: " + ex.toString(), ex);
        }
    }
}
