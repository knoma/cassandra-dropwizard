package com.knoma.web.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.cassandra.CassandraFactory;
import io.dropwizard.health.conf.HealthConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class WebConfig extends Configuration {

    @NotEmpty
    private String version;
    @Valid
    @NotNull
    @JsonProperty
    private CassandraFactory cassandra;

    @Valid
    @NotNull
    @JsonProperty("health")
    private HealthConfiguration healthConfiguration = new HealthConfiguration();

    public HealthConfiguration getHealthConfiguration() {
        return healthConfiguration;
    }

    public void setHealthConfiguration(final HealthConfiguration healthConfiguration) {
        this.healthConfiguration = healthConfiguration;
    }

    @JsonProperty
    public String getVersion() {
        return version;
    }

    @JsonProperty
    public void setVersion(String version) {
        this.version = version;
    }

    public CassandraFactory getCassandraFactory() {
        return cassandra;
    }

    public void setCassandraFactory(final CassandraFactory cassandraConfig) {
        this.cassandra = cassandraConfig;
    }
}
