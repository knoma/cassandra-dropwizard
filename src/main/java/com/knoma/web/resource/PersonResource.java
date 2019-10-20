package com.knoma.web.resource;

import com.knoma.web.dao.PersonAccessor;
import com.codahale.metrics.annotation.Timed;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.collect.ImmutableMap;
import com.knoma.web.pojo.Person;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/person")
public class PersonResource {

    private Session session;
    private PersonAccessor personAccessor;

    public PersonResource(Session session) {
        this.session = session;

        MappingManager manager = new MappingManager(session);
        this.personAccessor = manager.createAccessor(PersonAccessor.class);
    }

    @GET
    @Timed
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getPerson(@PathParam("id") String id) {
        System.out.println(personAccessor.getCount().one().getLong(0));
        return personAccessor.getById(UUID.fromString(id)).one();
    }

    @DELETE
    @Timed
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Long>  removePerson(@PathParam("id") String id) {
        personAccessor.delete(UUID.fromString(id));
        return ImmutableMap.of("count", personAccessor.getCount().one().getLong(0));
    }

    @GET
    @Timed
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPersons() {
        return personAccessor.getAll().all();
    }

    @GET
    @Timed
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Long> getPersonCount() {
        return ImmutableMap.of("count", personAccessor.getCount().one().getLong(0));
    }

    @POST
    @Timed
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    public Person addPerson(Person person) {
        personAccessor.save(person.getId(), person.getFirstName(), person.getLastName(), person.getEmail());
        return person;
    }
}
