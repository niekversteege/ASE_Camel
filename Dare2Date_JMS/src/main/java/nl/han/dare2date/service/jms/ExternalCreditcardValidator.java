/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;
import nl.han.dare2date.service.jms.util.JMSUtil;
import nl.han.dare2date.service.web.ValidateCreditcardService;

/**
 *
 * @author kaaz
 */
public class ExternalCreditcardValidator {
    public static void main(String[] args)
    {
        try {
            Connection conn = JMSUtil.getConnection();
            ValidateCreditcardService validateCreditcardService = new ValidateCreditcardService(conn);
        } catch (JMSException ex) {
            Logger.getLogger(ExternalCreditcardValidator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(ExternalCreditcardValidator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
