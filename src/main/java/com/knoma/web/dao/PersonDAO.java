package com.knoma.web.dao;

import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.knoma.web.pojo.Person;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Dao
public interface PersonDAO {

    @Select
    CompletionStage<Person> getById(UUID personId);

    @Insert
    void save(Person person);

    @Query("DELETE FROM cass_drop.person WHERE id = :id;")
    void delete(UUID id);

    @Query("SELECT count(id) FROM cass_drop.person;")
    CompletionStage<Long> getCount();

    @Query("SELECT * FROM cass_drop.person;")
    CompletionStage<MappedAsyncPagingIterable<Person>> getAll();
}
