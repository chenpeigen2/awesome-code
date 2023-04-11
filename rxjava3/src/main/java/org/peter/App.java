package org.peter;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;

public class App {

    final private static String TAG = "RXJAVA3";

    //    创建被观察者 （Observable ）并生产事件
    final static private Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            emitter.onNext(1);  //发送事件
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();//发送完成事件
        }
    });

    //    创建观察者 （Observer ）并响应事件
    final static private Observer<Integer> observer = new Observer<Integer>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            System.out.println("onSubscribe");
        }

        @Override
        public void onNext(@NonNull Integer integer) {
            System.out.println(integer);
            System.out.println("onNext");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            System.out.println("onError");
        }

        @Override
        public void onComplete() {
            System.out.println("onComplete");
        }
    };



//    onNext：用来发送数据，可多次调用，每调用一次发送一条数据
//
//    onError：用来发送异常通知，只发送一次，若多次调用只发送第一条
//
//    onComplete：用来发送完成通知，只发送一次，若多次调用只发送第一条

//    onError与onComplete互斥，两个方法只能调用一个不能同时调用，数据在发送时，出现异常可以调用onError发送异常通知也可以不调用，
//    因为其所在的方法subscribe会抛出异常，若数据在全部发送完之后均正常可以调用onComplete发送完成通知


    public static void main(String[] args) {
//        observable.subscribe(observer);

//        Disposable.fromRunnable()
//
//        Completable.create(new CompletableOnSubscribe() {
//            @Override
//            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
//                emitter.
//            }
//        })

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                emitter.onComplete();
            }
        }).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println(d.getClass());
                System.out.println("7777");
            }

            @Override
            public void onComplete() {
                System.out.println("555555");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("66666");
            }
        });
    }
}
