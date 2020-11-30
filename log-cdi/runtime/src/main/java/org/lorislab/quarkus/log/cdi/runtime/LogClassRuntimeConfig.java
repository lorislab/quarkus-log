package org.lorislab.quarkus.log.cdi.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public final class LogClassRuntimeConfig {

    public static LogClassRuntimeConfig create() {
        return new LogClassRuntimeConfig();
    }

    public static LogClassRuntimeConfig create(boolean disabled, boolean stacktrace) {
        LogClassRuntimeConfig config = new LogClassRuntimeConfig();
        config.disabled = disabled;
        config.stacktrace = stacktrace;
        return config;
    }

    /**
     * Enabled or disable log for the class.
     */
    @ConfigItem(name = "disabled", defaultValue = "false")
    public boolean disabled = false;

    /**
     * Enabled or disable stacktrace for the class.
     */
    @ConfigItem(name = "stacktrace", defaultValue = "true")
    public boolean stacktrace = true;
}
