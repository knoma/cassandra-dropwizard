package com.knoma.web;

import com.datastax.oss.driver.api.core.CqlSession;
import com.knoma.web.config.WebConfig;
import com.knoma.web.resource.PersonResource;
import io.dropwizard.cassandra.CassandraBundle;
import io.dropwizard.cassandra.CassandraFactory;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;

import org.glassfish.jersey.internal.inject.AbstractBinder;

public class WebApp extends Application<WebConfig> {

    public static void main(String[] args) throws Exception {
        new WebApp().run(args);
    }

    final CassandraBundle<WebConfig> cassandraBundle = new CassandraBundle<WebConfig>() {

        // Tells the bundle where to find the CassandraFactory within your WebConfig
        @Override
        public CassandraFactory getCassandraFactory(WebConfig configuration) {
            return configuration.getCassandraFactory();
        }
    };

    @Override
    public void run(WebConfig config, Environment env) {

        env.jersey().register("cassandra");

        env.jersey().register(PersonResource.class);
        env.jersey().register(new JsonProcessingExceptionMapper(true));
        env.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(cassandraBundle.getSession()).to(CqlSession.class);
            }
        });
    }

    @Override
    public void initialize(Bootstrap<WebConfig> bootstrap) {

        super.initialize(bootstrap);

        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false) // false = don't fail if env var missing
                )
        );
        bootstrap.addBundle(cassandraBundle);
    }
}