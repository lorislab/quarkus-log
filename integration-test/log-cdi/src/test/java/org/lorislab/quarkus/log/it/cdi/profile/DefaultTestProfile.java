package org.lorislab.quarkus.log.it.cdi.profile;

import io.quarkus.test.junit.QuarkusTestProfile;

public class DefaultTestProfile implements QuarkusTestProfile {

    @Override
    public String getConfigProfile() {
        return "test";
    }
}
