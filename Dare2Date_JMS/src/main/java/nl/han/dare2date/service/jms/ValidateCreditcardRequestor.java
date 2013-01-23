/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.jms;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.Creditcard;
import nl.han.dare2date.service.jms.util.Requestor;

public class ValidateCreditcardRequestor extends Requestor{
    private Creditcard cc;
    public ValidateCreditcardRequestor(Connection connection, String requestQueue, String replyQueue, String invalidQueue, Creditcard cc) 
            throws JMSException, NamingException {
        super(connection, requestQueue, replyQueue, invalidQueue);
        this.cc = cc;
    }

    @Override
 public ObjectMessage getObjectMessage() {
        ObjectMessage mssg = null;
        try {
            mssg = getSession().createObjectMessage();
            int[] content = {cc.getCvc(), cc.getNumber()};
            mssg.setObject(content);
        } catch (JMSException e) {
            System.out.println("WERKT NIET :(");
            e.printStackTrace();
        }
        return mssg;
    }


    @Override
    public Serializable getResponse() {
        try {
            ObjectMessage m = this.getReplyMessage();
            return m.getObject().toString();
        } catch (JMSException ex) {
            Logger.getLogger(ValidateCreditcardRequestor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
}
