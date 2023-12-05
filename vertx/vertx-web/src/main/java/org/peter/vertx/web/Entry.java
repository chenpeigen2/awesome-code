package org.peter.vertx.web;


import io.vertx.core.Vertx;


/**
 * only for trigger the entry
 **/
public class Entry {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HelloWorldVerticle.class.getName());
        vertx.deployVerticle(Verticle.class.getName());
    }


}
