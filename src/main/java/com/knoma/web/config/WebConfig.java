package com.knoma.web.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.cassandra.CassandraFactory;
import io.dropwizard.core.Configuration;

public class WebConfig extends Configuration {

    private final String version;
    private final CassandraFactory cassandra;

    @JsonCreator
    public WebConfig(
            @JsonProperty("version") String version,
            @JsonProperty("cassandra") CassandraFactory cassandra
    ) {
        this.version = version;
        this.cassandra = cassandra;
    }

    public String getVersion() {
        return version;
    }

    public CassandraFactory getCassandraFactory() {
        return cassandra;
    }
}
