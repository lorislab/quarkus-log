package org.lorislab.quarkus.log.it.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class TestService {

    public String test1() {
        return "TEST1";
    }

    String test2() {
        return "TEST2";
    }

    public String param1(String param) {
        return param + "X";
    }

    public static String staticMethod(String param1) {
        return "STATIC";
    }
}
