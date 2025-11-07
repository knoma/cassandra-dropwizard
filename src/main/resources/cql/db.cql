-- db.cql
CREATE KEYSPACE IF NOT EXISTS cass_drop
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
AND durable_writes = true;

CREATE TABLE IF NOT EXISTS cass_drop.person (
    id UUID PRIMARY KEY,
    email TEXT,
    first_name TEXT,
    last_name TEXT
);