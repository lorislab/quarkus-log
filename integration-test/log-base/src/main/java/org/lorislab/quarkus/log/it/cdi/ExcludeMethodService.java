package org.lorislab.quarkus.log.it.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExcludeMethodService {

    public String method(String p) {
        return "METHOD:" + p;
    }

    public String excludeMethod(String p) {
        return "EXCLUDE:" + p;
    }
}
