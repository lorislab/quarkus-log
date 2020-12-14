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

import org.lorislab.quarkus.log.LogExclude;
import org.lorislab.quarkus.log.cdi.interceptor.InterceptorContext;
import org.lorislab.quarkus.log.cdi.LogService;
import org.lorislab.quarkus.log.cdi.interceptor.LogConfig;
import org.lorislab.quarkus.log.cdi.interceptor.LogServiceInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.lang.reflect.Method;

import static org.lorislab.quarkus.log.rs.RestLogConfig.*;

/**
 * The rest log interceptor.
 */
@LogExclude
public class RestLogInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * The resource info.
     */
    @Context
    ResourceInfo resourceInfo;

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!endpoint().enabled) {
            return;
        }
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        if (endpoint().exclude(uri)) {
            return;
        }

        Class<?> clazz = resourceInfo.getResourceClass();
        LogService ano = LogServiceInterceptor.getLoggerServiceAno(clazz, clazz.getName(), resourceInfo.getResourceMethod());
        if (!ano.enabled()) {
            return;
        }
        requestContext.setProperty(ANO, ano);
        InterceptorContext context = new InterceptorContext(requestContext.getMethod(), uri);
        requestContext.setProperty(CONTEXT, context);
        // create the logger
        Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
        logger.info("{}", LogConfig.msg(endpoint().msgStart, new Object[]{context.method, requestContext.getUriInfo().getRequestUri(), requestContext.hasEntity()}));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        LogService ano = (LogService) requestContext.getProperty(ANO);
        if (ano == null || !ano.enabled()) {
            return;
        }
        InterceptorContext context = (InterceptorContext) requestContext.getProperty(CONTEXT);
        Response.StatusType status = responseContext.getStatusInfo();
        context.closeContext(status.getReasonPhrase());
        Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
        logger.info("{}", LogConfig.msg(endpoint().msgSucceed,
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
