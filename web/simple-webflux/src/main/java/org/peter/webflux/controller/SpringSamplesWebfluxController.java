package org.peter.webflux.controller;


import org.peter.webflux.models.User;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;


/**
 * 从代码中可以看到，使用 WebFlux 与 Spring MVC 的不同在于，WebFlux 所使用的类型是与反应式编程相关的 Flux 和 Mono 等，而不是简单的对象。
 * 对于简单的 Hello World 示例来说，这两者之间并没有什么太大的差别。
 * 对于复杂的应用来说，反应式编程和负压的优势会体现出来，可以带来整体的性能的提升
 */

@RestController
public class SpringSamplesWebfluxController {
    @GetMapping("/hello")
    public String handle() {
        return "Hello WebFlux";
    }

    @GetMapping("/hello_world")
    public Mono<String> sayHelloWorld() {
        return Mono.just("Hello World");
    }

    @GetMapping("/hello_world1")
    public Mono<ServerResponse> sendTimePerSec() {
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(  // 1
                Flux.interval(Duration.ofSeconds(1)).   // 2
                        map(l -> new SimpleDateFormat("HH:mm:ss").format(new Date())),
                String.class);
    }

    @GetMapping("/user")
    public Mono<User> getUser() {
        User u = new User();
        u.setId(1);
        u.setName("fucking amazing");
        return Mono.just(u);
    }

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getId());
    }

}
