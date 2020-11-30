package org.lorislab.quarkus.log.rs;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class RestLogMessageRuntimeConfig {

    /**
     * Started message
     */
    @ConfigItem(name = "start", defaultValue = "{0}({1}) started.")
    public String start;

    /**
     * Succeed message
     */
    @ConfigItem(name = "succeed", defaultValue = "{0}({1}):{2} [{3}s] succeed.")
    public String succeed;

}
