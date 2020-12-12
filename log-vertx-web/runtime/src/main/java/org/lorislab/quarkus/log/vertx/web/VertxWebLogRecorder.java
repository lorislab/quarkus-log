package org.lorislab.quarkus.log.vertx.web;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.ext.web.Router;

import javax.enterprise.inject.Default;
import java.util.regex.Pattern;

@Recorder
public class VertxWebLogRecorder {

    public void endpoint(BeanContainer container, RuntimeValue<Router> router, VertxWebLogRuntimeTimeConfig config) {
        if (!config.enabled) {
            return;
        }
        Pattern pattern = null;
        if (config.exclude.isPresent()) {
            pattern = Pattern.compile(config.exclude.get());
        }
        VertxWebLogConfig.endpoint(config);
//        VertxWebInterceptor interceptor = container.instance(VertxWebInterceptor.class, Default.Literal.INSTANCE);
        VertxWebInterceptor interceptor = new VertxWebInterceptor(pattern);
        router.getValue().route().order(-1 * config.priority).handler(interceptor::filter);

    }

}
