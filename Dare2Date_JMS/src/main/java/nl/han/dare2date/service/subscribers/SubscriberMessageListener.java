/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.subscribers;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import org.apache.log4j.Logger;

/**
 *
 * @author Niek
 */
public class SubscriberMessageListener implements MessageListener {
    
    private final Logger log = Logger.getLogger(getClass().getName());
    
    @Override
    public void onMessage(Message msg) {
        
        log.info("Received message:");
        
        if (msg instanceof ObjectMessage) {
            printMessage((ObjectMessage) msg);
        } else if (msg instanceof TextMessage) {
            printMessage((TextMessage) msg);
        } else {
            log.warn("Unknown message type received.");
        }
    }
    
    private void printMessage(ObjectMessage objectMessage) {
        
        try {
            Object msgObject = objectMessage.getObject();
            
            if (msgObject instanceof String) {
                printMessageContent((String) msgObject);
            } else {
                log.warn("Unknown object in message. Expected " + String.class.toString() + ". Received " + msgObject.getClass().toString());
            }
        } catch (JMSException ex) {
            log.error("Error reading ObjectMessage: " + ex.toString(), ex);
        }
        log.debug("\t");
    }
    
    private void printMessage(TextMessage textMessage) {
        try {
            printMessageContent(textMessage.getText());
        } catch (JMSException ex) {
            log.error("Error reading TextMessage: " + ex.toString(), ex);
        }
    }
    
    private void printMessageContent(String string) {
        log.info("\t" + string);
    }
}
