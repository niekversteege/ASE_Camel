package nl.han.dare2date.service.web;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.Creditcard;
import nl.han.dare2date.service.jms.ValidateCreditcardRequestor;
import nl.han.dare2date.service.jms.util.JMSUtil;
import nl.han.dare2date.service.jms.util.Queues;
import nl.han.dare2date.service.jms.util.Replier;

/**
 *
 * @author mdkr
 *
 * Is used as a JMS client using request-reply
 */
public class ValidateCreditcardService extends Replier implements Queues {

    private boolean lastvalid=false;
    /**
     *
     */
    
    public ValidateCreditcardService()throws JMSException, NamingException {
        super(JMSUtil.getConnection(), REQUEST_QUEUE, INVALID_QUEUE);
    }
    public ValidateCreditcardService(Connection c)throws JMSException, NamingException {
        super(c, REQUEST_QUEUE, INVALID_QUEUE);
    }

    public boolean validate(Creditcard cc) {
        try {
            Connection c = JMSUtil.getConnection();
             ValidateCreditcardRequestor vcr = new ValidateCreditcardRequestor(
                    c, Queues.REQUEST_QUEUE,
                    Queues.REPLY_QUEUE, Queues.INVALID_QUEUE, cc);
             vcr.send();
             vcr.receiveSync();
             c.close();
             return Boolean.parseBoolean(vcr.getResponse().toString());
        } catch (NamingException ex) {
            Logger.getLogger(ValidateCreditcardService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(ValidateCreditcardService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public boolean validatecc(Creditcard cc){
        return (cc.getCvc() == 1111 && cc.getNumber() == 1111);
    }
    @Override
    public ObjectMessage getReplyMessage() {
        try {
            ObjectMessage m;
            m = getSession().createObjectMessage();
            m.setObject(lastvalid);
            return m;
            
        } catch (JMSException ex) {
            Logger.getLogger(ValidateCreditcardService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void handleMessage(Serializable contents) {
        Creditcard cc = new Creditcard();
        int[] temp = (int[]) contents;
        cc.setCvc(temp[0]);
        cc.setNumber(temp[1]);
        lastvalid = validatecc(cc);
        
    }
}
