package nl.han.dare2date.service.web;

import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.Creditcard;
import nl.han.dare2date.service.jms.util.JMSUtil;
import nl.han.dare2date.service.jms.util.Queues;
import nl.han.dare2date.service.jms.util.Replier;
import org.apache.log4j.Logger;

/**
 *
 * @author mdkr
 *
 * Is used as a JMS client using request-reply
 */
public class ValidateCreditcardService extends Replier implements Queues {

    private final Logger log = Logger.getLogger(getClass().getName());
    private boolean lastvalid;

    public ValidateCreditcardService() throws JMSException, NamingException {
        super(JMSUtil.getConnection(), REQUEST_QUEUE, INVALID_QUEUE);
    }

    public ValidateCreditcardService(Connection c) throws JMSException, NamingException {
        super(c, REQUEST_QUEUE, INVALID_QUEUE);
    }

    public boolean validateCreditcard(Creditcard cc) {
        return (cc.getCvc() == 1234 && cc.getNumber() == 5678);
    }

    @Override
    public ObjectMessage getReplyMessage() {
        try {
            ObjectMessage m;
            m = getSession().createObjectMessage();
            m.setObject(lastvalid);
            return m;

        } catch (JMSException ex) {
            log.error("Error creating reply: " + ex.toString(), ex);
        }
        return null;
    }

    @Override
    public void handleMessage(Serializable contents) {
        Creditcard cc = (Creditcard) contents;
        //int[] temp = (int[]) contents;
        //cc.setCvc(temp[0]);
        //cc.setNumber(temp[1]);
        lastvalid = validateCreditcard(cc);

    }
}
