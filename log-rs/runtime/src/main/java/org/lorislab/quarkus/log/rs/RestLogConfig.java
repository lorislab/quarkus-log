package org.lorislab.quarkus.log.rs;

import org.lorislab.quarkus.log.cdi.runtime.LogClassRuntimeConfig;
import org.lorislab.quarkus.log.rs.runtime.RestLogRuntimeTimeConfig;
import org.lorislab.quarkus.log.rs.runtime.RestResourceRuntimeConfig;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class RestLogConfig {

    /**
     * The context interceptor property.
     */
    public static final String CONTEXT = "context";

    /**
     * The annotation interceptor property.
     */
    public static final String ANO = "ano";

    private static ConfigItem ENDPOINT = new ConfigItem();

    private static ConfigItem CLIENT = new ConfigItem();

    /**
     * The default constructor.
     */
    private RestLogConfig() {
        // empty constructor
    }

    public static void config(RestLogRuntimeTimeConfig config) {
        if (config.enabled) {
            ENDPOINT = new ConfigItem(config.message.start, config.message.succeed, config.priority, config.exclude);
        }
        if (config.client.enabled) {
            CLIENT = new ConfigItem(config.client.message.start, config.client.message.succeed, config.client.priority, config.client.exclude);
        }
    }

    public static ConfigItem endpoint() {
        return ENDPOINT;
    }

    public static ConfigItem client() {
        return CLIENT;
    }

    public static final class ConfigItem {

        /**
         * The message start.
         */
        public final MessageFormat msgStart;

        /**
         * The message succeed.
         */
        public final MessageFormat msgSucceed;

        /**
         * Disable or enable interceptor.
         */
        public final boolean enabled;

        /**
         * The interceptor priority.
         */
        public final int priority;

        /**
         * Exclude regex.
         */
        public final Pattern exclude;

        /**
         * Class configuration.
         */
        final Map<String, RestResourceRuntimeConfig> resources;

        ConfigItem() {
            this.enabled = false;
            this.priority = 100;
            this.msgSucceed = null;
            this.msgStart = null;
            this.exclude = null;
            this.resources = null;
        }

        ConfigItem(String msgStart, String msgSucceed, int priority, Optional<String> exclude) {
            this.enabled = true;
            this.priority = priority;
            this.msgSucceed = new MessageFormat(msgSucceed);
            this.msgStart = new MessageFormat(msgStart);
            this.exclude = exclude.map(Pattern::compile).orElse(null);
            this.resources = new HashMap<>();
        }

        public boolean exclude(String uri) {
            if (exclude == null) {
                return false;
            }
            return exclude.matcher(uri).matches();
        }

    }
}
