package com.knoma.web.managed;


import com.datastax.oss.driver.api.core.CqlSession;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

public class CassandraManager implements Managed {
    private final Logger log = LoggerFactory.getLogger(CassandraManager.class);

    private final CqlSession session;
    private final Duration shutdownGracePeriod;

    public CassandraManager(final CqlSession session, final Duration shutdownGracePeriod) {
        this.session = requireNonNull(session);
        this.shutdownGracePeriod = requireNonNull(shutdownGracePeriod);
    }

    @Override
    public void start() throws Exception {}

    @Override

    public void stop() throws Exception {
        log.debug("Attempting graceful shutdown of Cassandra session={}", session.getName());
        CompletionStage<Void> future = session.closeAsync();
        try {
            future.toCompletableFuture().get(shutdownGracePeriod.toMilliseconds(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.warn("Cassandra cluster did not close in gracePeriod={}. Forcing it now.", shutdownGracePeriod);
            session.forceCloseAsync();
        }
    }
}
