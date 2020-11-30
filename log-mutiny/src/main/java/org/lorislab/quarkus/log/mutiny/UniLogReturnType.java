package org.lorislab.quarkus.log.mutiny;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.AbstractUni;
import io.smallrye.mutiny.operators.UniSerializedSubscriber;
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.context.spi.ContextManagerProvider;
import org.lorislab.quarkus.log.LogExclude;
import org.lorislab.quarkus.log.LogReturnType;
import org.lorislab.quarkus.log.ReturnContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

@LogExclude
@ApplicationScoped
public class UniLogReturnType implements LogReturnType {

    static final ThreadContext THREAD_CONTEXT = ContextManagerProvider.instance().getContextManager()
            .newThreadContextBuilder().build();

    @Override
    public List<Class<?>> assignableClasses() {
        return List.of(Uni.class);
    }

    @Override
    public BiFunction<ReturnContext, Object, Object> function() {
        return (c, v) -> {
            Executor executor = THREAD_CONTEXT.currentContextExecutor();
            return new AbstractUni<>() {
                @Override
                protected void subscribing(UniSerializedSubscriber<? super Object> subscriber) {
                    WrapperSubscriber<Object> wrapper = new WrapperSubscriber<Object>(subscriber) {
                        @Override
                        public void onItem(Object item) {
                            super.onItem(item);
                            c.closeContext(item);
                        }

                        @Override
                        public void onFailure(Throwable failure) {
                            super.onFailure(failure);
                            c.errorContext(failure);
                        }
                    };
                    executor.execute(() -> AbstractUni.subscribe((Uni<?>) v, wrapper));
                }
            };
        };
    }
}
