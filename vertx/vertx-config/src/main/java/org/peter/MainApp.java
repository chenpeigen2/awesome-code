package org.peter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;


public class MainApp extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        var config = config();

        var nm = config.getInteger("a");

        System.out.println(nm);
    }
}
