package org.lorislab.quarkus.log.vertx.web;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.lorislab.quarkus.log.LogExclude;
import org.lorislab.quarkus.log.cdi.interceptor.InterceptorContext;
import org.lorislab.quarkus.log.cdi.interceptor.LogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import static org.lorislab.quarkus.log.vertx.web.VertxWebLogConfig.endpoint;

@LogExclude
@ApplicationScoped
public class VertxWebInterceptor {

    private static Logger LOGGER = LoggerFactory.getLogger(VertxWebInterceptor.class);

    public void filter(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        InterceptorContext context = new InterceptorContext(request.method().name(), request.uri());
        LOGGER.info("{}", LogConfig.msg(endpoint().msgStart, new Object[]{context.method, context.parameters, request.bytesRead() > 0}));
        rc.addEndHandler(e -> {
            context.closeContext(rc.response().getStatusMessage());
            LOGGER.info("{}", LogConfig.msg(endpoint().msgSucceed,
                    new Object[]{
                            context.method,
                            context.parameters,
                            context.time,
                            rc.response().getStatusCode(),
                            rc.response().getStatusMessage(),
                            rc.response().bytesWritten() > 0
                    }));
        });
        rc.next();

    }
}
