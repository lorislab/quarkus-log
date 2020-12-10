package org.lorislab.quarkus.log.it.cdi;

import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class StartEventService {

    @Inject
    TestService service;

    public void onStart(@Observes StartupEvent ev) {
        log.info("Start: " + service.test1());
    }

}
