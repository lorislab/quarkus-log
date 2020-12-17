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

import org.lorislab.quarkus.log.cdi.LogReplaceValue;
import org.lorislab.quarkus.log.cdi.ReturnContext;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
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
        if (!LogConfig.config().enabled) {
            return ic.proceed();
        }

        Method method = ic.getMethod();
        Class<?> clazz = getObjectClass(ic.getTarget(), method.getDeclaringClass());
        String className = getClassName(clazz);

        LogService ano = getLoggerServiceAno(clazz, className, method);
        if (!ano.enabled()) {
            return ic.proceed();
        }

        Logger logger = LoggerFactory.getLogger(className);
        String parameters = getValuesString(ic.getParameters(), method.getParameters());

        InterceptorContext context = new InterceptorContext(method.getName(), parameters);
        logger.info("{}", LogConfig.msgStart(context));

        boolean stacktrace = ano.stacktrace();
        try {
            return handleResult(logger, context, stacktrace, method.getReturnType(), ic.proceed());
        } catch (InvocationTargetException ie) {
            handleException(logger, context, stacktrace, ie.getCause());
            throw ie;
        } catch (Exception ex) {
            handleException(logger, context, stacktrace, ex);
            throw ex;
        }
    }

    private Object handleResult(Logger logger, InterceptorContext context, boolean stacktrace, Class<?> type, Object result) {
        BiFunction<ReturnContext, Object, Object> fn = logParamService.returnType(result);
        if (fn != null) {
            ReturnContext c = new ReturnContext() {
                @Override
                public void errorContext(Throwable t) {
                    handleException(logger, context, stacktrace, t);
                }

                @Override
                public void closeContext(Object value) {
                    handlerResult(logger, context, type, value);
                }
            };
            return fn.apply(c, result);
        }
        handlerResult(logger, context, type, result);
        return result;
    }

    private void handlerResult(Logger logger, InterceptorContext context, Class<?> type, Object result) {
        String contextResult = LogConfig.config().resultVoid;
        if (type != Void.TYPE) {
            contextResult = logParamService.getParameterValue(result);
        }
        context.closeContext(contextResult);
        logger.info("{}", LogConfig.msgSucceed(context));
    }

    /**
     * Handles the exception.
     *
     * @param context    the interceptor context.
     * @param logger     the logger.
     * @param stacktrace the stacktrace flag.
     * @param ex         the exception.
     */
    private void handleException(Logger logger, InterceptorContext context, boolean stacktrace, Throwable ex) {
        context.closeContext(logParamService.getParameterValue(ex));
        logger.error("{}", LogConfig.msgFailed(context));
        if (stacktrace) {
            logger.error("Error ", ex);
        }
    }

    /**
     * Gets the service class.
     *
     * @param object the target class.
     * @return the corresponding class name.
     */
    private static Class<?> getObjectClass(Object object, Class<?> methodClass) {
        if (object != null) {
            if (object instanceof Proxy) {
                Class<?>[] clazz = object.getClass().getInterfaces();
                if (clazz.length > 0) {
                    return clazz[0];
                }
            }
            return object.getClass();
        }
        return methodClass;
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
                return tmp.substring(0, tmp.length() - LogConfig.CDI_BEAN_SUFFIX.length());
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
        LogClassRuntimeConfig cc = LogConfig.config().get(methodKey);
        if (cc != null) {
            return createLoggerService(cc.enabled, cc.stacktrace);
        }
        cc = LogConfig.config().get(className);
        if (cc != null) {
            return createLoggerService(cc.enabled, cc.stacktrace);
        }

        // fallback check the annotation
        LogService an = method.getAnnotation(LogService.class);
        if (an != null) {
            LogConfig.config().put(methodKey, LogClassRuntimeConfig.create(an.enabled(), an.stacktrace()));
            return createLoggerService(an.enabled(), an.stacktrace());
        }
        an = clazz.getAnnotation(LogService.class);
        if (an != null) {
            LogConfig.config().put(className, LogClassRuntimeConfig.create(an.enabled(), an.stacktrace()));
            return createLoggerService(an.enabled(), an.stacktrace());
        }
        return createLoggerService(true, true);
    }

    /**
     * Creates the logger service.
     *
     * @param enabled    the log enabled flag.
     * @param stacktrace the stacktrace flag.
     * @return the corresponding logger service.
     */
    private static LogService createLoggerService(boolean enabled, boolean stacktrace) {
        return new LogService() {
            @Override
            public boolean enabled() {
                return enabled;
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
        if (pa == null) {
            return logParamService.getParameterValue(value);
        }
        if (!pa.mask().isEmpty()) {
            return pa.mask();
        }
        return parameter.getName();
    }

}
