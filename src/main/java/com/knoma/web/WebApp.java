package com.knoma.web;

import brave.Tracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.knoma.web.config.WebConfig;
import com.knoma.web.resource.PersonResource;
import io.dropwizard.Application;
import io.dropwizard.health.conf.HealthConfiguration;
import io.dropwizard.health.core.HealthCheckBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.IOException;

public class WebApp extends Application<WebConfig> {

    private static final Tracing tracing = Tracing.newBuilder()
            .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                    .addScopeDecorator(StrictScopeDecorator.create())
                    .build())
            .build();
    private Session session;

    public static void main(String[] args) throws Exception {
        new WebApp().run(args);
    }

    @Override
    public void run(WebConfig config, Environment env) throws IOException {

        this.session = config.getCassandraFactory().build(env.metrics(), env.lifecycle(),
                env.healthChecks(), tracing);

        final PersonResource personResource = new PersonResource(session);
        env.jersey().register(personResource);
        env.healthChecks();

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