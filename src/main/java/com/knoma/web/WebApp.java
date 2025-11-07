package com.knoma.web;

import com.knoma.web.config.WebConfig;
import io.dropwizard.cassandra.CassandraBundle;
import io.dropwizard.cassandra.CassandraFactory;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;

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

        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(new MainModule(cassandraBundle))
                .build());

    }
}