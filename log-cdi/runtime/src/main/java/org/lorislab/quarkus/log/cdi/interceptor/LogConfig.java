/*
 * Copyright 2020 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.quarkus.log.cdi.interceptor;

import org.lorislab.quarkus.log.cdi.runtime.LogClassRuntimeConfig;
import org.lorislab.quarkus.log.cdi.runtime.LogRuntimeTimeConfig;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * The logger configuration.
 *
 */
public class LogConfig {

    public static String CDI_BEAN_SUFFIX = "_Subclass";

    private static ConfigItem CONFIG = new ConfigItem();

    /**
     * The default constructor.
     */
    private LogConfig() {
        // empty constructor
    }

    // RUNTIME_INIT
    public static void config(LogRuntimeTimeConfig config, Map<String, LogClassRuntimeConfig> classes) {
        CONFIG = new ConfigItem(
                config.message.start,
                config.message.succeed,
                config.message.failed,
                config.message.returnVoid,
                config.classConfig,
                classes);
    }

    public static ConfigItem config() {
        return CONFIG;
    }

    /**
     * The message failed method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static Object msgFailed(InterceptorContext context) {
        return msg(CONFIG.msgFailed, new Object[]{context.method, context.parameters, context.result, context.time});
    }

    /**
     * The message succeed method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static Object msgSucceed(InterceptorContext context) {
        return msg(CONFIG.msgSucceed, new Object[]{context.method, context.parameters, context.result, context.time});
    }

    /**
     * The message start method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static Object msgStart(InterceptorContext context) {
        return msg(CONFIG.msgStart, new Object[]{context.method, context.parameters});
    }

    /**
     * Log message method.
     *
     * @param mf         the message formatter.
     * @param parameters the log entry parameters.
     * @return the log parameter.
     */
    public static Object msg(MessageFormat mf, Object[] parameters) {
        return new Object() {
            @Override
            public String toString() {
                return mf.format(parameters, new StringBuffer(), null).toString();
            }
        };
    }


    public static final class ConfigItem {

        /**
         * The result void text.
         */
        public final String resultVoid;

        /**
         * Class configuration.
         */
        final Map<String, LogClassRuntimeConfig> classConfig;

        /**
         * The message start.
         */
        public final MessageFormat msgStart;

        /**
         * The message succeed.
         */
        public final MessageFormat msgSucceed;

        /**
         * The message failed.
         */
        public final MessageFormat msgFailed;

        public final boolean enabled;


        ConfigItem() {
            this.enabled = false;
            this.msgSucceed = null;
            this.msgFailed = null;
            this.msgStart = null;
            this.resultVoid = null;
            this.classConfig = new HashMap<>();
        }

        ConfigItem(String msgStart, String msgSucceed, String msgFailed, String resultVoid, Map<String, LogClassRuntimeConfig> classConfig, Map<String, LogClassRuntimeConfig> classes) {
            this.enabled = true;
            this.resultVoid = resultVoid;
            this.msgSucceed = new MessageFormat(msgSucceed);
            this.msgStart = new MessageFormat(msgStart);
            this.msgFailed = new MessageFormat(msgFailed);
            this.classConfig = new HashMap<>();
            // configuration classes from source code
            this.classConfig.putAll(classes);
            // configuration application.properties
            this.classConfig.putAll(classConfig);
        }

        public LogClassRuntimeConfig get(String key) {
            return classConfig.get(key);
        }

        public void put(String key, LogClassRuntimeConfig config) {
            classConfig.put(key, config);
        }
    }
}

