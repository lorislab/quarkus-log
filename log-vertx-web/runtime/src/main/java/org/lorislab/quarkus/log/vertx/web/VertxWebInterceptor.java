package org.lorislab.quarkus.log.vertx.web;

import io.quarkus.arc.Unremovable;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.lorislab.quarkus.log.cdi.interceptor.InterceptorContext;
import org.lorislab.quarkus.log.cdi.interceptor.LogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static org.lorislab.quarkus.log.vertx.web.VertxWebLogConfig.endpoint;

@Unremovable
public class VertxWebInterceptor {

    private static Logger LOGGER = LoggerFactory.getLogger(VertxWebInterceptor.class);

    private final Pattern exclude;

    VertxWebInterceptor(Pattern exclude) {
        this.exclude = exclude;
    }

    public void filter(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        if (exclude == null || !exclude.matcher(request.uri()).matches()) {

            String content = request.getHeader("Content-Length");
            boolean body = content != null && !"0".equals(content);

            InterceptorContext context = new InterceptorContext(request.method().name(), request.uri());
            LOGGER.info("{}", LogConfig.msg(endpoint().msgStart, new Object[]{context.method, context.parameters, body }));
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
        }
        rc.next();
    }
}
