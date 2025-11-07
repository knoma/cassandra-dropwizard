package com.knoma.web.resource;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.knoma.web.dao.PersonDAO;
import com.knoma.web.dao.PersonMapper;
import com.knoma.web.dao.PersonMapperBuilder;
import com.knoma.web.pojo.Person;
import jakarta.inject.Inject;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class PersonService implements PersonDAO {


    private final PersonDAO personDAO;

    @Inject
    public PersonService(CqlSession session) {
        PersonMapper personMapper = new PersonMapperBuilder(session).build();
        this.personDAO = personMapper.personDao("cass_drop", "person");
    }

    @Override
    public CompletionStage<Person> getById(UUID personId) {
        return personDAO.getById(personId);
    }

    @Override
    public CompletionStage<Void> saveAsync(Person person) {
        return personDAO.saveAsync(person);
    }

    @Override
    public CompletionStage<Void> delete(UUID id) {
        return personDAO.delete(id);
    }

    @Override
    public CompletionStage<Long> getCount() {
        return personDAO.getCount();
    }

    @Override
    public CompletionStage<MappedAsyncPagingIterable<Person>> getAll() {
        return personDAO.getAll();
    }
}
