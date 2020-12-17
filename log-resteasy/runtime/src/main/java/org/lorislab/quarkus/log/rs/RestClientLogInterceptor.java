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

import org.lorislab.quarkus.log.cdi.LogExclude;
import org.lorislab.quarkus.log.cdi.interceptor.InterceptorContext;
import org.lorislab.quarkus.log.cdi.interceptor.LogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;

import static org.lorislab.quarkus.log.rs.RestLogConfig.*;

/**
 * The rest client log interceptor
 *
 * @author Andrej Petras
 */
@LogExclude
public class RestClientLogInterceptor implements ClientRequestFilter, ClientResponseFilter {

    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(RestClientLogInterceptor.class);

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext) {
        if (!client().enabled) {
            return;
        }
        String uri = requestContext.getUri().toString();
        if (client().exclude(uri)) {
            return;
        }
        InterceptorContext context = new InterceptorContext(requestContext.getMethod(),uri);
        requestContext.setProperty(CONTEXT, context);
        log.info("{}", LogConfig.msg(client().msgStart, new Object[]{requestContext.getMethod(), requestContext.getUri(), requestContext.hasEntity()}));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        InterceptorContext context = (InterceptorContext) requestContext.getProperty(CONTEXT);
        if (context == null) {
            return;
        }
        Response.StatusType status = responseContext.getStatusInfo();
        context.closeContext(status.getReasonPhrase());
        log.info("{}", LogConfig.msg(client().msgSucceed, new Object[]{context.method, requestContext.getUri(), context.time, status.getStatusCode(), context.result, responseContext.hasEntity()}));
    }
}
