package com.knoma.web.resource;

import com.codahale.metrics.annotation.Timed;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.google.common.collect.ImmutableMap;
import com.knoma.web.dao.PersonDAO;
import com.knoma.web.dao.PersonMapper;
import com.knoma.web.dao.PersonMapperBuilder;
import com.knoma.web.pojo.Person;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/person")
public class PersonResource {

    private PersonDAO personDAO;

    public PersonResource(CqlSession session) {
        PersonMapper personMapper = new PersonMapperBuilder(session).build();
        this.personDAO = personMapper.personDao(CqlIdentifier.fromCql("cass_drop"));
    }

    @GET
    @Timed
    @ManagedAsync
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getPerson(@Suspended final AsyncResponse response, @PathParam("id") UUID id) {
         response.resume(Response.status(Response.Status.OK).entity(personDAO.getById(id)).build());
    }

    @DELETE
    @Timed
    @ManagedAsync
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void removePerson(@Suspended final AsyncResponse response, @PathParam("id") String id) {
        personDAO.delete(UUID.fromString(id));
        response.resume(Response.status(Response.Status.OK).entity(ImmutableMap.of("count", personDAO.getCount())).build());
    }

    @GET
    @Timed
    @ManagedAsync
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public void getPersons(@Suspended final AsyncResponse response) {
        response.resume(Response.status(Response.Status.OK).entity(personDAO.getAll().all()).build());
    }

    @POST
    @Timed
    @ManagedAsync
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    public void addPerson(@Suspended final AsyncResponse response, Person person) {
        personDAO.save(person);
        response.resume(Response.status(Response.Status.CREATED).entity(person).build());
    }

    @GET
    @Timed
    @ManagedAsync
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public void getPersonCount(@Suspended final AsyncResponse response) {
        response.resume(Response.status(Response.Status.OK).entity(ImmutableMap.of("count", personDAO.getCount())).build());
    }
}
