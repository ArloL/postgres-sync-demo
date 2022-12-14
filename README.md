# postgres-sync-demo

A demo on how to use triggers, queues, etc. to sync the app's data somewhere else.

# Context

Imagine you have a system MyMovieDb (MDB).

MyMovieDb uses a postgres database.

Now we want to create the system MyMovieWatchList (WL).

WL wants to use data from MDB.

WL wants to be certain it has gotten _all_ the data from MDB.

WL wants to still work even if MDB is down (e.g maintenance).

So WL can't use a REST API or similiar.

WL has an unspecified datastore.

# Decision

A postgres trigger is used to create another table entry as part of the
transaction when data is changed. Due to transactionality the "queue table"
entry is only added if the data was actually changed.

To update the target, select an entry from the queue table and insert/update/delete
the data. The entry is only deleted from the queue if the write to the target
was successful (e.g. commit).

To allow parallel processing the rows are locked with `SELECT ... FOR UPDATE`.

To skip locked rows `... SKIP LOCKED` is used.

The entry is immediately deleted via `DELETE FROM ... USING ... RETURNING`.

To process the entries immediately `NOTIFY/LISTEN` is used.

Every 60 seconds the table is checked for events where the `NOTIFY` was missed.

You can find this implemented in `postgres-schema.sql` and
`MovieSyncEventRepository.java`.

`LISTEN` is implemented in `MovieSyncEventPostgresR2dbcNotificationListener.java`.

The scheduled check is implemented in `MovieSyncServiceTrigger.java`.

To see it in action run `MovieSyncServiceTest.java`.

# Alternatives not chosen

* Write data to e.g. RabbitMQ or Kafka as part of the transaction or after the commit
    * when the write to RabbitMQ/Kafka fails there is no way of knowing which data is not in sync anymore
    * this is a two phase commit style problem
* Debezium could be used but was not chosen due to a small number of writes

# Consequences

* We can't easily add more consumers.
    * We could write the data to e.g. RabbitMQ when we process the queue table entries
    * This shows that the proposed design is evolvable.
* The target datastore must be fine with data being only partially in sync
* You can't parallelize writing the target datastore - otherwise a DELETE can move before the INSERT

# Performance

Run in a Docker environment.

Just inserts: 903-912 operations/second

Inserts and the trigger to write to the queue table: 812-833 operations/second

Inserts, the trigger and direct sync to a different table: 186-185 operations/second

Inserts, the trigger and sync via RabbitMQ to a different table: 118-126 operations/second

# Resources

* <https://brandur.org/job-drain>
* <https://dagster.io/blog/skip-kafka-use-postgres-message-queue>
* <https://blog.ankushthakur.com/posts/postgres-as-message-queue/>
* <https://www.crunchydata.com/blog/message-queuing-using-native-postgresql>
* <https://wiki.postgresql.org/wiki/Audit_trigger>
* <https://jschmitz.dev/posts/testcontainers_how_to_use_them_in_your_spring_boot_integration_tests/>
* <https://www.postgresql.org/message-id/flat/1011786.1669743348%40sss.pgh.pa.us#2cd138e1d2604ccca005407222a56e2f>
