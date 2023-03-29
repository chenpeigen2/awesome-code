package featuretest.flow;

import java.util.concurrent.Flow;
import java.util.function.Function;

@Core
public class MappingProcessor<P, N> implements Flow.Processor<P, N> {

    private final Function<P, N> function;
    private Flow.Subscriber<? super N> subscriber;
    private Flow.Subscription subscription;

    public MappingProcessor(Function<P, N> function) {
        this.function = function;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super N> subscriber) {
        this.subscriber = subscriber;
        this.subscriber.onSubscribe(subscription);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("x2");
        this.subscription = subscription;
    }

    @Override
    public void onNext(P item) {
        subscriber.onNext(function.apply(item));
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
