package org.lorislab.quarkus.log.it.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExcludeService {

    public String test(String p) {
        return "EXCLUDE:" + p;
    }
}
