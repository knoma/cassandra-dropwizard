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
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.inject.Singleton;

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

        bootstrap.addBundle(new HealthCheckBundle<WebConfig>() {
            @Override
            protected HealthConfiguration getHealthConfiguration(final WebConfig configuration) {
                return configuration.getHealthConfiguration();
            }
        });
    }
}