package org.lorislab.quarkus.log.rs;

import java.text.MessageFormat;

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

    public static void endpoint(RestLogRuntimeTimeConfig config) {
        ENDPOINT = new ConfigItem(config.message.start, config.message.succeed, config.priority);
    }
    public static void client(RestLogRuntimeTimeConfig config) {
        CLIENT = new ConfigItem(config.client.message.start, config.client.message.succeed, config.client.priority);
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

        public final boolean enabled;

        public final int priority;

        ConfigItem() {
            this.enabled = false;
            this.priority = 100;
            this.msgSucceed = null;
            this.msgStart = null;
        }

        ConfigItem(String msgStart, String msgSucceed, int priority) {
            this.enabled = true;
            this.priority = priority;
            this.msgSucceed = new MessageFormat(msgSucceed);
            this.msgStart = new MessageFormat(msgStart);
        }
    }
}
