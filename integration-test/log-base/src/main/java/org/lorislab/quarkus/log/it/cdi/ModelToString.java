package org.lorislab.quarkus.log.it.cdi;

public class ModelToString {

    public String param;

    public ModelToString(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "Model{" +
                "param='" + param + '\'' +
                '}';
    }
}
