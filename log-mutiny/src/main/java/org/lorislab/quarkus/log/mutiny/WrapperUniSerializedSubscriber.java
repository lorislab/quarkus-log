package org.lorislab.quarkus.log.mutiny;

import io.smallrye.mutiny.operators.UniSerializedSubscriber;
import io.smallrye.mutiny.subscription.UniSubscriber;
import io.smallrye.mutiny.subscription.UniSubscription;

public class WrapperUniSerializedSubscriber<T> implements UniSubscriber<T>, UniSubscription  {

    UniSerializedSubscriber<T> delegate;

    public WrapperUniSerializedSubscriber(UniSerializedSubscriber<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onSubscribe(UniSubscription subscription) {
        delegate.onSubscribe(subscription);
    }

    @Override
    public void onItem(T item) {
        delegate.onItem(item);
    }

    @Override
    public void onFailure(Throwable failure) {
        delegate.onFailure(failure);
    }

    @Override
    public void cancel() {
        delegate.cancel();
    }
}
