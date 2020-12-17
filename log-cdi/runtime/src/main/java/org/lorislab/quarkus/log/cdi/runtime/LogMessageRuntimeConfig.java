package org.lorislab.quarkus.log.cdi.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class LogMessageRuntimeConfig {

    /**
     * Started message
     */
    @ConfigItem(defaultValue = "void")
    public String returnVoid;

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


    /**
     * Failed message
     */
    @ConfigItem(name = "failed", defaultValue = "{0}({1}):{2} [{3}s] failed.")
    public String failed;
}
