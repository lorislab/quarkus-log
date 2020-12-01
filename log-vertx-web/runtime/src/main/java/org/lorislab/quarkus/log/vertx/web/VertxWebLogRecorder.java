package org.lorislab.quarkus.log.vertx.web;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.ext.web.Router;

import javax.enterprise.inject.Default;

@Recorder
public class VertxWebLogRecorder {

    public void endpoint(BeanContainer container, RuntimeValue<Router> router, VertxWebLogRuntimeTimeConfig config) {
        VertxWebLogConfig.endpoint(config);
        VertxWebInterceptor interceptor = container.instance(VertxWebInterceptor.class, Default.Literal.INSTANCE);
        router.getValue().route().order(-1 * config.priority).handler(interceptor::filter);
    }

}
