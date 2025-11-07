package com.knoma.web;

import com.datastax.oss.driver.api.core.CqlSession;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.knoma.web.config.WebConfig;
import io.dropwizard.cassandra.CassandraBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

public class MainModule extends DropwizardAwareModule<WebConfig> {

    private final CassandraBundle<WebConfig> cassandraBundle;

    public MainModule(CassandraBundle<WebConfig> cassandraBundle) {
        this.cassandraBundle = cassandraBundle;
    }

    @Override
    protected void configure() {
        environment().jersey().register(new JsonProcessingExceptionMapper(true));
    }

    @Provides
    @Singleton
    public CqlSession provideCqlSession() {
        return cassandraBundle.getSession();
    }
}
