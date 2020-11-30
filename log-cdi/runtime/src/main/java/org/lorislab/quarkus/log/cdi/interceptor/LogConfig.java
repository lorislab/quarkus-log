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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The logger configuration.
 *
 */
public class LogConfig {

    public static String CDI_BEAN_SUFFIX = "_Subclass";

    /**
     * The result void text.
     */
    public static String RESULT_VOID;

    /**
     * The message start.
     */
    public static MessageFormat MSG_START;

    /**
     * The message succeed.
     */
    public static MessageFormat MSG_SUCCEED;

    /**
     * The message failed.
     */
    public static MessageFormat MSG_FAILED;

    /**
     * Class configuration.
     */
    public static Map<String, LogClassRuntimeConfig> CLASS_CONFIG = new HashMap<>();

    /**
     * The default constructor.
     */
    private LogConfig() {
        // empty constructor
    }

    public static void config(Map<String, LogClassRuntimeConfig> classes) {
        CLASS_CONFIG.putAll(classes);
    }

    public static void config(LogRuntimeTimeConfig config) {
        MSG_START = new MessageFormat(config.start);
        MSG_SUCCEED = new MessageFormat(config.succeed);
        MSG_FAILED = new MessageFormat(config.failed);
        RESULT_VOID = config.returnVoid;
        CLASS_CONFIG.putAll(config.classConfig);
    }

    /**
     * The message failed method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static Object msgFailed(InterceptorContext context) {
        return msg(MSG_FAILED, new Object[]{context.method, context.parameters, context.result, context.time});
    }

    /**
     * The message succeed method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static Object msgSucceed(InterceptorContext context) {
        return msg(MSG_SUCCEED, new Object[]{context.method, context.parameters, context.result, context.time});
    }

    /**
     * The message start method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static Object msgStart(InterceptorContext context) {
        return msg(MSG_START, new Object[]{context.method, context.parameters});
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

}

