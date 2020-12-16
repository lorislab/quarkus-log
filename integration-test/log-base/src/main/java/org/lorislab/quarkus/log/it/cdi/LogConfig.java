package org.lorislab.quarkus.log.it.cdi;

import org.lorislab.quarkus.log.cdi.LogParamValue;

import javax.enterprise.inject.Produces;

import static org.lorislab.quarkus.log.cdi.LogParamValue.assignable;

public class LogConfig {

    @Produces
    public LogParamValue model() {
        return assignable((v) -> "model:" + ((Model)v).param, Model.class);
    }
}
