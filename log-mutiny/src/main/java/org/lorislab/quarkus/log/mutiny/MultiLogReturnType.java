package org.lorislab.quarkus.log.mutiny;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.StrictMultiSubscriber;
import io.smallrye.mutiny.operators.AbstractMulti;
import io.smallrye.mutiny.subscription.MultiSubscriber;
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.context.spi.ContextManagerProvider;
import org.lorislab.quarkus.log.cdi.LogExclude;
import org.lorislab.quarkus.log.cdi.LogReturnType;
import org.lorislab.quarkus.log.cdi.ReturnContext;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

@LogExclude
@ApplicationScoped
public class MultiLogReturnType implements LogReturnType {

    static final ThreadContext THREAD_CONTEXT = ContextManagerProvider.instance().getContextManager()
            .newThreadContextBuilder().build();

    @Override
    public List<Class<?>> assignableClasses() {
        return List.of(Multi.class);
    }

    @Override
    public BiFunction<ReturnContext, Object, Object> function() {
        return (c, v) -> {
            Executor executor = THREAD_CONTEXT.currentContextExecutor();
            return new ContextPropagationMulti<>(executor, (Multi<?>) v, c);
        };
    }

    private static class ContextPropagationMulti<T> extends AbstractMulti<T> {

        private final Executor executor;
        private final Multi<T> multi;
        private final ReturnContext c;

        public ContextPropagationMulti(Executor executor, Multi<T> multi, ReturnContext c) {
            this.executor = executor;
            this.multi = multi;
            this.c = c;
        }

        @Override
        public void subscribe(Subscriber<? super T> subscriber) {
            Objects.requireNonNull(subscriber);
            WrapperMultiSubscriber<T> wrapper = new WrapperMultiSubscriber<T>(subscriber) {

                @Override
                public void onComplete() {
                    c.closeContext(null);
                    super.onComplete();
                }

                @Override
                public void onError(Throwable t) {
                    c.errorContext(t);
                    super.onError(t);
                }
            };

            executor.execute(() -> {
                if (subscriber instanceof MultiSubscriber) {
                    multi.subscribe(wrapper);
                } else {
                    multi.subscribe(new StrictMultiSubscriber<>(wrapper));
                }
            });
        }
    }
}


