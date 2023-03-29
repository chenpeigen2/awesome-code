package featuretest.flow;

import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FlowHelper<T> {

    // because publish can be middle state ,so publish should be changed
    private Flow.Publisher<T> publisher;

    public static <E> FlowHelper<E> flow(Spliterator<E> spliterator) {
        return new FlowHelper<>(new FlowPublisher<>(spliterator));
    }

    private FlowHelper(Flow.Publisher<T> publisher) {
        this.publisher = publisher;
    }

    public FlowHelper<T> filter(Predicate<T> predicate) {
        FilterProcessor<T> p = new FilterProcessor<>(predicate);
        publisher.subscribe(p);
        publisher = p;
        return this;
    }

    public <R> FlowHelper<R> mapping(Function<T, R> function) {
        MappingProcessor<T, R> p = new MappingProcessor<>(function);
        publisher.subscribe(p);
        return new FlowHelper<>(p);
    }

    public void accept(Consumer<T> consumer) {
        FlowSubscriber<T> s = new FlowSubscriber<>(consumer);
        publisher.subscribe(s);
    }
}
