package org.lorislab.quarkus.log.rs.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public final class RestResourceRuntimeConfig {

    public static RestResourceRuntimeConfig create() {
        return new RestResourceRuntimeConfig();
    }

    public static RestResourceRuntimeConfig create(boolean enabled, boolean stacktrace) {
        RestResourceRuntimeConfig config = new RestResourceRuntimeConfig();
        config.enabled = enabled;
        config.stacktrace = stacktrace;
        return config;
    }

    /**
     * Enabled or disable log for the class.
     */
    @ConfigItem(name = "enabled", defaultValue = "true")
    public boolean enabled = true;

    /**
     * Enabled or disable stacktrace for the class.
     */
    @ConfigItem(name = "stacktrace", defaultValue = "true")
    public boolean stacktrace = true;
}
