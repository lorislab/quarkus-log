package org.lorislab.quarkus.log.it.concurrent;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
public class ConcurrentService {

    public CompletableFuture<Void> test1(String param) {
        return CompletableFuture.runAsync(() -> {
            log.info("Execute {}", param);
        });
    }

}
