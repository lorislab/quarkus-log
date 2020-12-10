package org.lorislab.quarkus.log.it.cdi;

public class SubModel extends Model {

    public String param2;

    public SubModel(String param, String param2) {
        super(param);
        this.param2 = param2;
    }
}
