package org.lorislab.quarkus.log.vertx.web;

import java.text.MessageFormat;

public class VertxWebLogConfig {

    /**
     * The context interceptor property.
     */
    public static final String CONTEXT = "context";

    private static ConfigItem ENDPOINT = new ConfigItem();

    /**
     * The default constructor.
     */
    private VertxWebLogConfig() {
        // empty constructor
    }

    public static void endpoint(VertxWebLogRuntimeTimeConfig config) {
        ENDPOINT = new ConfigItem(config.message.start, config.message.succeed, config.priority);
    }

    public static ConfigItem endpoint() {
        return ENDPOINT;
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
