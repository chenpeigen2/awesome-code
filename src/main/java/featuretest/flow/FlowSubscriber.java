package featuretest.flow;

import java.util.concurrent.Flow;
import java.util.function.Consumer;

@Core
public class FlowSubscriber<T> implements Flow.Subscriber<T> {

    private final Consumer<T> consumer;
    private Flow.Subscription subscription;
    private boolean completed = false;
    private long requestCount;
    private static final long REQUEST_COUNT = 3;

    public FlowSubscriber(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("x3");
        requestCount = REQUEST_COUNT;
        this.subscription = subscription;
        this.subscription.request(requestCount); // here 1
    }

    @Override
    public void onNext(T item) {
        requestCount--;
        consumer.accept(item);
        if (requestCount == 0 && !completed) {
            requestCount = REQUEST_COUNT;
            subscription.request(requestCount);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {
        requestCount = 0;
        completed = true;
    }
}
