package org.lorislab.quarkus.log.rs.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class RestLogMessageRuntimeConfig {

    /**
     * Started message
     */
    @ConfigItem(name = "start", defaultValue = "{0} {1} [{2}] started.")
    public String start;

    /**
     * Succeed message
     */
    @ConfigItem(name = "succeed", defaultValue = "{0} {1} [{2}s] finished [{3}-{4},{5}].")
    public String succeed;

}
