/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.jms;

import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.Creditcard;
import nl.han.dare2date.service.jms.util.Requestor;
import org.apache.log4j.Logger;

public class ValidateCreditcardRequestor extends Requestor {

    private final Logger log = Logger.getLogger(getClass().getName());
    private Creditcard creditcard;

    public ValidateCreditcardRequestor(Connection connection, String requestQueue, String replyQueue, String invalidQueue, Creditcard cc)
            throws JMSException, NamingException {
        super(connection, requestQueue, replyQueue, invalidQueue);
        this.creditcard = cc;
    }

    @Override
    public ObjectMessage getObjectMessage() {
        ObjectMessage message = null;
        try {
            message = getSession().createObjectMessage();
            int[] content = {creditcard.getCvc(), creditcard.getNumber()};
            message.setObject(content);
        } catch (JMSException ex) {
            log.error("Error getting ObjectMessage: " + ex.toString(), ex);
        }
        return message;
    }

    @Override
    public Serializable getResponse() {
        try {
            ObjectMessage m = this.getReplyMessage();
            return m.getObject().toString();
        } catch (JMSException ex) {
            log.error("Error creating response: " + ex.toString(), ex);
        }
        return null;

    }
}
