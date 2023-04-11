package org.peter;

import io.reactivex.rxjava3.core.Completable;

public class MemoryTest {
    public static void main(String[] args) {
        Completable.complete().subscribe(AODMemoryObserver.from());
        Completable.complete().subscribe(AODMemoryObserver.from());
        Completable.complete().subscribe(AODMemoryObserver.from());
    }
}
