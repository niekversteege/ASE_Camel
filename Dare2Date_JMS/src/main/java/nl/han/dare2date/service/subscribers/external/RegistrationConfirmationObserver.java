/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.subscribers.external;

import java.util.Observable;
import nl.han.dare2date.service.jms.util.DurableObserver;
import org.apache.log4j.Logger;

/**
 *
 * @author Niek
 */
public class RegistrationConfirmationObserver extends DurableObserver {

    private final Logger log = Logger.getLogger(getClass().getName());
    private String name;

    public RegistrationConfirmationObserver(String name) {
        this.name = name;
    }

    @Override
    public String getSubscriberName() {
        return name;
    }

    @Override
    public void update(Observable o, Object arg) {
        
        log.info("Received a message:\n\t" + arg.toString());
    }
}
