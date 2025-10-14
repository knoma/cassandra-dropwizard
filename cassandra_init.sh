#!/bin/sh
set -e

CASSANDRA_HOST="${CASSANDRA_HOST:-cassandra}"
CASSANDRA_PORT="${CASSANDRA_PORT:-9042}"
CQL_USER="${CQL_USER:-cassandra}"
CQL_PASS="${CQL_PASS:-cassandra}"

echo "[cassandra-init] Waiting for Cassandra to accept connections at $CASSANDRA_HOST:$CASSANDRA_PORT..."

# Wait until 'describe cluster' succeeds
until cqlsh "$CASSANDRA_HOST" "$CASSANDRA_PORT" -u "$CQL_USER" -p "$CQL_PASS" -e "describe cluster" > /dev/null 2>&1; do
  echo "[cassandra-init] Cassandra not yet available... retrying in 5s"
  sleep 5
done

echo "[cassandra-init] Cassandra is available. Running schema initialization..."

cqlsh "$CASSANDRA_HOST" "$CASSANDRA_PORT" -u "$CQL_USER" -p "$CQL_PASS" <<'EOF'
CREATE KEYSPACE IF NOT EXISTS cass_drop
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
  AND durable_writes = true;

CREATE TABLE IF NOT EXISTS cass_drop.person (
    id         uuid PRIMARY KEY,
    email      text,
    first_name text,
    last_name  text
);
EOF

echo "[cassandra-init] Schema created successfully."
echo "[cassandra-init] Cassandra initialization complete."