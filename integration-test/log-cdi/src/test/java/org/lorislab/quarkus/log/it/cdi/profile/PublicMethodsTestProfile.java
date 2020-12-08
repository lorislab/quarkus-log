package org.lorislab.quarkus.log.it.cdi.profile;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class PublicMethodsTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("quarkus.lorislab.log.only-public-method","false",
                "quarkus.lorislab.log.packages", "org.lorislab,test");
    }

    @Override
    public String getConfigProfile() {
        return "pm";
    }
}
