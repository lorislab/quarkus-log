package org.lorislab.quarkus.log.cdi.common;

import org.lorislab.quarkus.log.cdi.LogExclude;
import org.lorislab.quarkus.log.cdi.LogReturnType;
import org.lorislab.quarkus.log.cdi.ReturnContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

@LogExclude
@ApplicationScoped
public class CompletionStageLogReturnType implements LogReturnType {

    @Override
    public List<Class<?>> assignableClasses() {
        return List.of(CompletionStage.class);
    }

    @Override
    public BiFunction<ReturnContext, Object, Object> function() {
        return (c, v) -> {
            CompletionStage<?> cs = (CompletionStage<?>) v;
            cs.toCompletableFuture().whenComplete((u, eex) -> {
                if (eex != null) {
                    c.errorContext(eex);
                } else {
                    c.closeContext(u);
                }
            });
            return v;
        };
    }
}
