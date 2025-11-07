package com.knoma.web.resource;

import com.codahale.metrics.annotation.Timed;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.knoma.web.dao.PersonDAO;
import com.knoma.web.dao.PersonMapper;
import com.knoma.web.dao.PersonMapperBuilder;
import com.knoma.web.pojo.Person;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Path("/person")
public class PersonResource {

    private final PersonDAO personDAO;

    @Inject
    public PersonResource(CqlSession session) {
        PersonMapper personMapper = new PersonMapperBuilder(session).build();
        this.personDAO = personMapper.personDao(CqlIdentifier.fromCql("cass_drop"));
    }

    @GET
    @Timed
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Response> getPerson(@PathParam("id") UUID id) {
        return personDAO.getById(id)
                .thenApply(person -> Response.ok(person != null ? person : "{}").build());
    }

    @DELETE
    @Timed
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Response> removePerson(@PathParam("id") String id) {
        return personDAO.delete(UUID.fromString(id))
                .thenCompose(v -> personDAO.getCount())
                .thenApply(count -> Response.ok(ImmutableMap.of("count", count)).status(Response.Status.OK).build());
    }

    @GET
    @Timed
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Response> getPersons() {
        return personDAO.getAll()
                .thenApply(MappedAsyncPagingIterable::currentPage)
                .thenApply(persons -> Response.ok(persons).status(Response.Status.OK).build());
    }

    @PUT
    @Timed
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    public CompletionStage<Response> addPerson(Person person) {
        return personDAO.saveAsync(person)
                .thenApply(v -> Response.ok(person).status(Response.Status.CREATED).build());
    }

    @GET
    @Timed
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Response> getPersonCount() {
        return personDAO.getCount()
                .thenApply(count -> Response.ok(ImmutableMap.of("count", count)).status(Response.Status.OK).build());
    }
}