package org.lorislab.quarkus.log.it.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExtTestService {

    public String start(long id) {
        return "EXTERNAL";
    }
}
