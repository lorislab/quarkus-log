package test;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class ExternalService {

    public String external() {
        return "EXTERNAL";
    }

}
