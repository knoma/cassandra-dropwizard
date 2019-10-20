package com.knoma.web;

import com.datastax.oss.driver.api.core.CqlSession;
import com.knoma.web.config.WebConfig;
import com.knoma.web.health.CassandraHealthCheck;
import com.knoma.web.managed.CassandraManager;
import com.knoma.web.resource.PersonResource;
import io.dropwizard.Application;
import io.dropwizard.health.conf.HealthConfiguration;
import io.dropwizard.health.core.HealthCheckBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class WebApp extends Application<WebConfig> {

    public static void main(String[] args) throws Exception {
        new WebApp().run(args);
    }

    protected String validationQuery = "SELECT key FROM system.local;";

    @Override
    public void run(WebConfig config, Environment env) {

        CqlSession session = CqlSession.builder().build();

        env.lifecycle().manage(new CassandraManager(session, io.dropwizard.util.Duration.milliseconds(1000)));

        final PersonResource personResource = new PersonResource(session);
        env.jersey().register(personResource);

        final CassandraHealthCheck healthCheck = new CassandraHealthCheck(session, validationQuery, io.dropwizard.util.Duration.milliseconds(1000));
        env.healthChecks().register("cassandra", healthCheck);

        env.jersey().register(new JsonProcessingExceptionMapper(true));
    }

    @Override
    public void initialize(Bootstrap<WebConfig> bootstrap) {
        super.initialize(bootstrap);

        bootstrap.addBundle(new HealthCheckBundle<WebConfig>() {
            @Override
            protected HealthConfiguration getHealthConfiguration(final WebConfig configuration) {
                return configuration.getHealthConfiguration();
            }
        });
    }
}