package com.knoma.web.dao;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.knoma.web.pojo.Person;

import java.util.UUID;

@Dao
public interface PersonDAO {

    @Select
    Person getById(UUID personId);

    @Insert
    void save(Person person);

    @Query("DELETE FROM cass_drop.person WHERE id = :id;")
    void delete(UUID id);

    @Query("SELECT count(id) FROM cass_drop.person;")
    Long getCount();

    @Query("SELECT * FROM cass_drop.person;")
    PagingIterable<Person> getAll();
}
