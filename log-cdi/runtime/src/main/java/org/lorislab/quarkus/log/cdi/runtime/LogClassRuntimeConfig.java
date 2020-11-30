package org.lorislab.quarkus.log.cdi.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public final class LogClassRuntimeConfig {

    public static LogClassRuntimeConfig create() {
        return new LogClassRuntimeConfig();
    }

    public static LogClassRuntimeConfig create(boolean log, boolean stacktrace) {
        LogClassRuntimeConfig config = new LogClassRuntimeConfig();
        config.log = log;
        config.stacktrace = stacktrace;
        return config;
    }

    /**
     * Enabled or disable log for the class.
     */
    @ConfigItem(name = "log", defaultValue = "true")
    public boolean log = true;

    /**
     * Enabled or disable stacktrace for the class.
     */
    @ConfigItem(name = "stacktrace", defaultValue = "true")
    public boolean stacktrace = true;
}
