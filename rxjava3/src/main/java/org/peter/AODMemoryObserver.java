package org.peter;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class AODMemoryObserver implements CompletableObserver {


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println(this);
        System.out.println("1");
    }

    @Override
    public void onComplete() {
        System.out.println("2");
    }

    @Override
    public void onError(@NonNull Throwable e) {

    }

    private static volatile CompletableObserver mObserver;

    public static synchronized CompletableObserver from() {
        if (mObserver == null) mObserver = new AODMemoryObserver();
        return mObserver;
    }
}