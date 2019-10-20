package com.knoma.web.dao;

import com.knoma.web.pojo.Person;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;

import java.util.UUID;

@Accessor
public interface PersonAccessor {

    @Query("SELECT * FROM cass_drop.person WHERE id = :id")
    Result<Person> getById(@Param("id") UUID userId);

    @Query("INSERT INTO cass_drop.person (id, first_name, last_name, email)  VALUES (:id, :firstname, :lastname, :email)")
    void save(@Param("id") UUID userId, @Param("firstname") String firstName, @Param("lastname") String lastName, @Param("email") String email);

    @Query("DELETE FROM cass_drop.person WHERE id = :id")
    void delete(@Param("id") UUID userId);

    @Query("SELECT count(*) FROM cass_drop.person;")
    ResultSet getCount();

    @Query("SELECT * FROM cass_drop.person;")
    Result<Person> getAll();
}
