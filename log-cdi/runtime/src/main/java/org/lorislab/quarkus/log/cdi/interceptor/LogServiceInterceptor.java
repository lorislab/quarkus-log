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

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lorislab.quarkus.log.ReturnContext;
import org.lorislab.quarkus.log.LogReplaceValue;
import org.lorislab.quarkus.log.cdi.LogService;
import org.lorislab.quarkus.log.cdi.runtime.LogClassRuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * The logger service interceptor.
 *
 * @author Andrej Petras
 */
@LogService
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class LogServiceInterceptor {

    /**
     * The logger builder service.
     */
    @Inject
    LogParamValueService logParamService;

    /**
     * The method execution.
     *
     * @param ic the invocation context.
     * @return the method result object.
     * @throws Exception if the method fails.
     */
    @AroundInvoke
    public Object methodExecution(final InvocationContext ic) throws Exception {
        Object result;
        Method method = ic.getMethod();
        System.out.println("######### --> " + method.getName() + " -> " + ic.getTarget());
        String className = getObjectClassName(ic.getTarget());

        LogService ano = getLoggerServiceAno(ic.getTarget().getClass(), className, method);
        if (ano.log()) {

            Logger logger = LoggerFactory.getLogger(className);
            String parameters = getValuesString(ic.getParameters(), method.getParameters());

            InterceptorContext context = new InterceptorContext(method.getName(), parameters);
            logger.info("{}", LogConfig.msgStart(context));

            try {
                result = ic.proceed();
                result = handleResult(logger, context, ano, method.getReturnType(), result);
            } catch (InvocationTargetException ie) {
                handleException(context, logger, ano, ie.getCause());
                throw ie;
            } catch (Exception ex) {
                handleException(context, logger, ano, ex);
                throw ex;
            }
        } else {
            result = ic.proceed();
        }
        return result;
    }

    private Object handleResult(Logger logger, InterceptorContext context, LogService ano, Class<?> type, Object result) {
        BiFunction<ReturnContext, Object, Object> fn = logParamService.returnType(result);
        if (fn != null) {
            ReturnContext c = new ReturnContext() {

                @Override
                public void errorContext(Throwable t) {
                    handleException(context, logger, ano, t);
                }

                @Override
                public void closeContext(Object value) {
                    String contextResult = LogConfig.RESULT_VOID;
                    if (type != Void.TYPE) {
                        contextResult = getValue(value);
                    }
                    context.closeContext(contextResult);
                    // log the success message
                    logger.info("{}", LogConfig.msgSucceed(context));
                }
            };
            return fn.apply(c, result);
        }

        // default
        String contextResult = LogConfig.RESULT_VOID;
        if (type != Void.TYPE) {
            contextResult = getValue(result);
        }
        context.closeContext(contextResult);
        // log the success message
        logger.info("{}", LogConfig.msgSucceed(context));
        return result;
    }

    /**
     * Handles the exception.
     *
     * @param context the interceptor context.
     * @param logger  the logger.
     * @param ano     the annotation.
     * @param ex      the exception.
     */
    private void handleException(InterceptorContext context, Logger logger, LogService ano, Throwable ex) {
        context.closeContext(getValue(ex));
        logger.error("{}", LogConfig.msgFailed(context));
        boolean stacktrace = ano.stacktrace();
        if (stacktrace) {
            logger.error("Error ", ex);
        }
    }

    /**
     * Gets the service class name.
     *
     * @param object the target class.
     * @return the corresponding class name.
     */
    private static String getObjectClassName(Object object) {
        if (object instanceof Proxy) {
            Class<?>[] clazz = object.getClass().getInterfaces();
            if (clazz.length > 0) {
                return getClassName(clazz[0]);
            }
        }
        return getClassName(object.getClass());
    }

    /**
     * Gets the service class name.
     *
     * @param clazz the target class.
     * @return the corresponding class name.
     */
    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            String tmp = clazz.getName();
            if (tmp.endsWith(LogConfig.CDI_BEAN_SUFFIX)) {
                return tmp.substring(0, tmp.length() -  LogConfig.CDI_BEAN_SUFFIX.length());
            }
            return tmp;
        }
        return null;
    }

    /**
     * Gets the logger service annotation.
     *
     * @param clazz  the class.
     * @param method the method.
     * @return the logger service annotation.
     */
    public static LogService getLoggerServiceAno(Class<?> clazz, String className, Method method) {

        String methodKey = className + "." + method.getName();
        LogClassRuntimeConfig cc = LogConfig.CLASS_CONFIG.get(methodKey);
        if (cc != null) {
            return createLoggerService(cc.log, cc.stacktrace);
        }
        cc = LogConfig.CLASS_CONFIG.get(className);
        if (cc != null) {
            return createLoggerService(cc.log, cc.stacktrace);
        }

        System.out.println("########################\n Fallback: " + methodKey + "\n####################");
        System.out.println("#\n" + LogConfig.CLASS_CONFIG+ "\n$$###");
        // fallback check the annotation
        LogService an = method.getAnnotation(LogService.class);
        if (an != null) {
            LogConfig.CLASS_CONFIG.put(methodKey, LogClassRuntimeConfig.create(an.log(), an.stacktrace()));
            return createLoggerService(an.log(), an.stacktrace());
        }
        an = clazz.getAnnotation(LogService.class);
        if (an != null) {
            LogConfig.CLASS_CONFIG.put(className, LogClassRuntimeConfig.create(an.log(), an.stacktrace()));
            return createLoggerService(an.log(), an.stacktrace());
        }
        return createLoggerService(true, true);
    }

    /**
     * Creates the logger service.
     *
     * @param log        the log flag.
     * @param stacktrace the stacktrace flag.
     * @return the corresponding logger service.
     */
    private static LogService createLoggerService(boolean log, boolean stacktrace) {
        return new LogService() {
            @Override
            public boolean log() {
                return log;
            }

            @Override
            public boolean stacktrace() {
                return stacktrace;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return LogService.class;
            }
        };
    }

    /**
     * Gets the list of string corresponding to the list of parameters.
     *
     * @param value      the list of parameters.
     * @param parameters the list of method parameters.
     * @return the list of string corresponding to the list of parameters.
     */
    private String getValuesString(Object[] value, Parameter[] parameters) {
        if (value != null && value.length > 0) {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            sb.append(getValue(value[index], parameters[index]));
            index++;
            for (; index < value.length; index++) {
                sb.append(',');
                sb.append(getValue(value[index], parameters[index]));
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Get the parameter log value.
     *
     * @param value     the parameter value.
     * @param parameter the method parameter.
     * @return the corresponding log value.
     */
    private String getValue(Object value, Parameter parameter) {
        LogReplaceValue pa = parameter.getAnnotation(LogReplaceValue.class);
        if (pa != null) {
            if (!pa.mask().isEmpty()) {
                return pa.mask();
            }
            return parameter.getName();
        }
        return getValue(value);
    }

    /**
     * Gets the string corresponding to the parameter.
     *
     * @param parameter the method parameter.
     * @return the string corresponding to the parameter.
     */
    private String getValue(Object parameter) {
        return logParamService.getParameterValue(parameter);
    }
}
