package org.lorislab.quarkus.log.mutiny;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class WrapperMultiSubscriber<T> implements Subscriber<T> {

    Subscriber<? super T> delegate;

    public WrapperMultiSubscriber(Subscriber<? super T> delegate) {
        this.delegate = delegate;
    }


    @Override
    public void onSubscribe(Subscription s) {
        delegate.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        delegate.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        delegate.onError(t);
    }

    @Override
    public void onComplete() {
        delegate.onComplete();
    }
}
