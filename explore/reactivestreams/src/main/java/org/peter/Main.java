package org.peter;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class Main {
    public static void main(String[] args) {
        Flux.just("a", "b", "c").subscribe(new Subscriber<String>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                subscription.request(1); // 请求1个
            }

            @Override
            public void onNext(String s) {
                System.out.println(s); // 响应
                subscription.request(1); // 再请求1个
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("completed"); // 完成
            }
        });
    }
}