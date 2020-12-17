package org.lorislab.quarkus.log.it.rs;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class TestService {

    public String test1() {
        return "TEST1";
    }

    public String post(String param, String body) {
        return param + "X" + body;
    }
}
