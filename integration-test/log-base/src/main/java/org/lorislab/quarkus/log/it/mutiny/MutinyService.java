package org.lorislab.quarkus.log.it.mutiny;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
public class MutinyService {

    public Uni<String> test1(String param) {
        return Uni.createFrom().item(param)
                .map(x -> {
                    log.info("Execute {}", param);
                    return x;
                });
    }

}
