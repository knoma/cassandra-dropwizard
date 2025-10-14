[![Build Status](https://travis-ci.com/knoma/cassandra-dropwizard.svg?branch=Cassandra4)](https://travis-ci.com/knoma/cassandra-dropwizard)
# REST Webservice using Dropwizard and Cassandra
Simple REST Webservice using Dropwizard 4.0 beta and Cassandra Driver 4.15

###Requirements 
 - Cassandra 
 - Java 25
 - Curl
 - Docker
 
Apply keyspace and table from [cql folder](src/main/resources/cql/db.cql).
```shell script
./cqlsh -f src/main/resources/cql/db.cql
```

Build app via gradle
```shell script
./gradlew clean build
```

Start service via the fat jar
```shell script
java -jar build/libs/cassandra-dropwizard-all.jar server config.yml
```

Check health of the service 
```shell script
curl http://localhost:9000/health-check
```

Create data via curl
```shell script
curl -v  -XPOST "localhost:9000/person" -H "Content-Type: application/json" -d '{"id": "66992983-af17-43ad-9fc9-b9a654a42d36", "firstName": "how", "lastName": "Ilove", "email": "jss@test.de"}'
```

Query single person
```shell script
curl -v "localhost:9000/person/66992983-af17-43ad-9fc9-b9a654a42d36"
```

Query all persons
```shell script
curl -v "localhost:9000/person/all"
```
Query count
```shell script
curl -v  "localhost:9000/person/count"
```

DELETE data
```shell script
curl -v -XDELETE  "localhost:9000/person/66992983-af17-43ad-9fc9-b9a654a42d36"
```

