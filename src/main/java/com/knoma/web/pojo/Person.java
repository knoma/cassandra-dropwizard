package com.knoma.web.pojo;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@Entity
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public final class Person {

    @PartitionKey
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    public Person() {
    }

    public Person(UUID id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @JsonProperty public UUID getId() { return id; }
    @JsonProperty public String getFirstName() { return firstName; }
    @JsonProperty public String getLastName() { return lastName; }
    @JsonProperty public String getEmail() { return email; }
}