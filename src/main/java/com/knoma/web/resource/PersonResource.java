package com.knoma.web.resource;

import com.codahale.metrics.annotation.Timed;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.google.common.collect.ImmutableMap;
import com.knoma.web.dao.PersonDAO;
import com.knoma.web.dao.PersonMapper;
import com.knoma.web.dao.PersonMapperBuilder;
import com.knoma.web.pojo.Person;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getPerson(@PathParam("id") UUID id) {
        return personDAO.getById(id);
    }

    @DELETE
    @Timed
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Long> removePerson(@PathParam("id") String id) {
        personDAO.delete(UUID.fromString(id));
        return ImmutableMap.of("count", personDAO.getCount());
    }

    @GET
    @Timed
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPersons() {
        return personDAO.getAll().all();
    }

    @GET
    @Timed
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Long> getPersonCount() {
        return ImmutableMap.of("count", personDAO.getCount());
    }

    @POST
    @Timed
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    public Person addPerson(Person person) {
        personDAO.save(person);
        return person;
    }
}
