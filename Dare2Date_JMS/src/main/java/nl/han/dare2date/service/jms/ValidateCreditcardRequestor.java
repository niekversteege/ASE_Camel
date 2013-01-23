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
import nl.han.dare2date.service.jms.util.Requestor;

public class ValidateCreditcardRequestor extends Requestor {

    public ValidateCreditcardRequestor(Connection connection, String requestQueue, String replyQueue, String invalidQueue)
            throws JMSException, NamingException {
        super(connection, requestQueue, replyQueue, invalidQueue);
    }

    @Override
    public ObjectMessage getObjectMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Serializable getResponse() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
