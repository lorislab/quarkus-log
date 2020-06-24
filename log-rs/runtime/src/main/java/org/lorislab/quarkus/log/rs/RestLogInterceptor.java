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
package org.lorislab.quarkus.log.rs;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lorislab.quarkus.log.cdi.interceptor.InterceptorContext;
import org.lorislab.quarkus.log.cdi.interceptor.LogConfig;
import org.lorislab.quarkus.log.cdi.LogService;
import org.lorislab.quarkus.log.cdi.interceptor.LogServiceInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;

/**
 * The rest log interceptor.
 */
@LogService(log = false)
public class RestLogInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    static final String PROP_DISABLE_PROTECTED_METHODS = "lorislab.log.protected.disable";

    /**
     * The annotation interceptor property.
     */
    private static final String ANO = "ano";

    /**
     * The context interceptor property.
     */
    private static final String CONTEXT = "context";

    /**
     * The message start.
     */
    private static MessageFormat messageStart;

    /**
     * The message succeed.
     */
    private static MessageFormat messageSucceed;

    static {
        Config config = ConfigProvider.getConfig();
        messageStart = new MessageFormat(config.getOptionalValue("lorislab.log.rs.start", String.class).orElse("{0} {1} [{2}] started."));
        messageSucceed = new MessageFormat(config.getOptionalValue("lorislab.log.rs.succeed", String.class).orElse("{0} {1} [{2}s] finished [{3}-{4},{5}]."));
    }

    /**
     * The resource info.
     */
    @Context
    private ResourceInfo resourceInfo;

    /**
     * The rest logger interceptor disable flag.
     */
    @Inject
    @ConfigProperty(name = "lorislab.log.rs.disable", defaultValue = "false")
    private boolean disable;

    @Inject
    @ConfigProperty(name = PROP_DISABLE_PROTECTED_METHODS, defaultValue = "true")
    boolean disableProtectedMethod;

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (disable) {
            return;
        }
        LogService ano = LogServiceInterceptor.getLoggerServiceAno(resourceInfo.getResourceClass(), resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod(), disableProtectedMethod);
        requestContext.setProperty(ANO, ano);

        if (ano.log()) {
            InterceptorContext context = new InterceptorContext(requestContext.getMethod(), requestContext.getUriInfo().getRequestUri().toString());
            requestContext.setProperty(CONTEXT, context);

            // create the logger
            Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
            logger.info("{}", LogConfig.msg(messageStart, new Object[]{context.method, requestContext.getUriInfo().getRequestUri(), requestContext.hasEntity()}));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (disable) {
            return;
        }
        LogService ano = (LogService) requestContext.getProperty(ANO);
        if (ano != null && ano.log()) {
            InterceptorContext context = (InterceptorContext) requestContext.getProperty(CONTEXT);
            Response.StatusType status = responseContext.getStatusInfo();
            context.closeContext(status.getReasonPhrase());
            Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
            logger.info("{}", LogConfig.msg(messageSucceed,
                    new Object[]{
                            context.method,
                            context.parameters,
                            context.time,
                            status.getStatusCode(),
                            status.getReasonPhrase(),
                            responseContext.hasEntity()
                    }));
        }
    }

}
