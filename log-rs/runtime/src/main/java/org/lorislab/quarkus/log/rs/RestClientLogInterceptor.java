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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;

/**
 * The rest client log interceptor
 *
 * @author Andrej Petras
 */
@LogService(disabled = true)
public class RestClientLogInterceptor implements ClientRequestFilter, ClientResponseFilter {

    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(RestClientLogInterceptor.class);

    /**
     * The context interceptor property.
     */
    private static final String CONTEXT = "context";

    /**
     * The message start.
     */
    private MessageFormat messageStart;

    /**
     * The message succeed.
     */
    private MessageFormat messageSucceed;

    public RestClientLogInterceptor() {
        Config config = ConfigProvider.getConfig();
        messageStart = new MessageFormat(config.getOptionalValue("org.lorislab.jel.logger.rs.client.start", String.class).orElse("{0} {1} [{2}] started."));
        messageSucceed = new MessageFormat(config.getOptionalValue("org.lorislab.jel.logger.rs.client.succeed", String.class).orElse("{0} {1} finished in [{2}s] with [{3}-{4},{5}]."));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext) {
        if (!RestLogConfig.client().enabled) {
            return;
        }
        InterceptorContext context = new InterceptorContext(requestContext.getMethod(), requestContext.getUri().toString());
        requestContext.setProperty(CONTEXT, context);
        log.info("{}", LogConfig.msg(RestLogConfig.client().msgStart, new Object[]{requestContext.getMethod(), requestContext.getUri(), requestContext.hasEntity()}));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        if (!RestLogConfig.client().enabled) {
            return;
        }
        InterceptorContext context = (InterceptorContext) requestContext.getProperty(CONTEXT);
        if (context != null) {
            Response.StatusType status = responseContext.getStatusInfo();
            context.closeContext(status.getReasonPhrase());
            log.info("{}", LogConfig.msg(RestLogConfig.client().msgSucceed, new Object[]{context.method, requestContext.getUri(), context.time, status.getStatusCode(), context.result, responseContext.hasEntity()}));
        }
    }
}
