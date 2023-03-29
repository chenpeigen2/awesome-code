package featuretest.flow;

import java.util.concurrent.Flow;
import java.util.function.Predicate;

@Core
public class FilterProcessor<T> implements Flow.Processor<T, T>, Flow.Subscription {

    private final Predicate<T> predicate;
    private Flow.Subscriber<? super T> subscriber;
    private Flow.Subscription subscription;
    private long requestCount;
    private long leftCount;

    public FilterProcessor(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    /**
     * publish method
     *
     * @param subscriber the subscriber
     */
    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        this.subscriber.onSubscribe(this);
    }

    /***      start   subscriber method   *******/
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("x1");
        this.subscription = subscription;
    }

    @Override
    public void onNext(T item) { // invoke next onNext
        requestCount--;
        if (predicate.test(item)) { // here we start
            subscriber.onNext(item);
        } else {
            leftCount++;
        }
        if (requestCount == 0 && leftCount > 0) {
            request(leftCount);
        }
    }

    @Override
    public void onError(Throwable throwable) { // invoke next onError
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() { // invoke next onComplete
        subscriber.onComplete();
    }

    /**
     * Subscription method
     *
     * @param n the increment of demand; a value of {@code
     *          Long.MAX_VALUE} may be considered as effectively unbounded
     */
    @Override
    public void request(long n) {
        leftCount = 0;
        requestCount = n;
        subscription.request(n);  // to invoke next subscriber onSubscribe here 2
    }

    /**
     * Subscription method
     */
    @Override
    public void cancel() {
        subscription.cancel();
    }
}
