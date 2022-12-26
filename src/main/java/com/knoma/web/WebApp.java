package com.knoma.web;

import com.datastax.oss.driver.api.core.CqlSession;
import com.knoma.web.config.WebConfig;
import com.knoma.web.health.CassandraHealthCheck;
import com.knoma.web.managed.CassandraManager;
import com.knoma.web.resource.PersonResource;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;

import org.glassfish.jersey.internal.inject.AbstractBinder;

import static io.dropwizard.util.Duration.milliseconds;

public class WebApp extends Application<WebConfig> {

    protected String validationQuery = "SELECT key FROM system.local;";

    private CqlSession session = CqlSession.builder().build();

    public static void main(String[] args) throws Exception {
        new WebApp().run(args);
    }

    @Override
    public void run(WebConfig config, Environment env) {

        env.lifecycle().manage(new CassandraManager(session, milliseconds(1000)));
        env.jersey().register(PersonResource.class);

        final CassandraHealthCheck healthCheck = new CassandraHealthCheck(session, validationQuery, milliseconds(1000));
        env.healthChecks().register("cassandra", healthCheck);

        env.jersey().register(new JsonProcessingExceptionMapper(true));

        env.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(session).to(CqlSession.class);
            }
        });
    }

    @Override
    public void initialize(Bootstrap<WebConfig> bootstrap) {
        super.initialize(bootstrap);
    }
}