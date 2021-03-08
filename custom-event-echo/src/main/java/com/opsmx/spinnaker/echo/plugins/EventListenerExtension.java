package com.opsmx.spinnaker.echo.plugins;


import com.netflix.spinnaker.echo.api.events.Event;
import com.netflix.spinnaker.echo.api.events.EventListener;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class EventListenerExtension implements EventListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void processEvent(Event event) {
        log.info(" process event ");
        log.info(" event id : " + event.getEventId());
        log.info(" event payload " + event.getPayload());
        log.info(" event details " + event.getDetails());
        log.info(" event content " + event.getContent());
        log.info(" event rawString " + event.getRawContent());
    }
}
