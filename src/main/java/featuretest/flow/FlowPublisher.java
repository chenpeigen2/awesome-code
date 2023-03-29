package featuretest.flow;

import java.util.Spliterator;
import java.util.concurrent.Flow;

public class FlowPublisher<T> implements Flow.Publisher<T>, Flow.Subscription {

    private final Spliterator<T> spliterator;
    private Flow.Subscriber<? super T> subscriber;
    private boolean completed = false;
    private boolean canceled = false;

    public FlowPublisher(Spliterator<T> spliterator) {
        this.spliterator = spliterator;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        this.subscriber.onSubscribe(this);
    }

    @Override
    public void request(long n) {
        //已完成或已取消，则不再下发消息
        if (completed || canceled) {
            return;
        }
        //数据消息下发完，则下发onComplete消息
        long size = spliterator.getExactSizeIfKnown();
        if (size == 0) {
            completed = true;
            subscriber.onComplete();
            return;
        }
        //下发数据消息
        for (long i = Math.min(n, size); i > 0; i--) {
            System.out.println(); // here 3
            spliterator.tryAdvance(subscriber::onNext);
        }
    }

    @Override
    public void cancel() {
        canceled = true;
        //取消后下发onComplete消息
        if (!completed) {
            subscriber.onComplete();
        }
    }
}
