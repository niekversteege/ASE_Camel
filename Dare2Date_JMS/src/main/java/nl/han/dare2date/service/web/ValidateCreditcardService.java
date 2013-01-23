package nl.han.dare2date.service.web;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.Creditcard;
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

    /**
     *
     */
    public ValidateCreditcardService() throws JMSException, NamingException {
        super(JMSUtil.getConnection(), REQUEST_QUEUE, INVALID_QUEUE);
    }

    public boolean validate(Creditcard cc) {
        return true;
    }

    @Override
    public ObjectMessage getReplyMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handleMessage(Serializable contents) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}