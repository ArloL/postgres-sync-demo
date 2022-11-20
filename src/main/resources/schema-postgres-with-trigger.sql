-- !! This file uses two semicolons as separators !!

DROP TABLE IF EXISTS "movie";;

DROP TABLE IF EXISTS "watch_list";;

DROP TABLE IF EXISTS "movie_sync_event";;

CREATE TABLE "movie" (
	"id" BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	"title" VARCHAR(128) NOT NULL
);;

CREATE TABLE "watch_list" (
	"id" BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	"movie_id" BIGINT NOT NULL,
	"title" VARCHAR(128) NOT NULL
);;

CREATE TABLE "movie_sync_event" (
	"id" BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	"action" TEXT NOT NULL check (action in ('I','D','U')),
	"movie_id" BIGINT NOT NULL
);;

CREATE OR REPLACE FUNCTION movie_sync_function()
	RETURNS TRIGGER
	LANGUAGE PLPGSQL
	AS
$$
BEGIN
	PERFORM pg_notify('movie_sync_event_channel', NULL);
	if (TG_OP = 'INSERT') then
		INSERT INTO movie_sync_event(movie_id, action) VALUES (NEW.id, substring(TG_OP,1,1));
		RETURN NEW;
	elsif (TG_OP = 'UPDATE') then
    	INSERT INTO movie_sync_event(movie_id, action) VALUES (NEW.id, substring(TG_OP,1,1));
		RETURN NEW;
    elsif (TG_OP = 'DELETE') then
		INSERT INTO movie_sync_event(movie_id, action) VALUES (OLD.id, substring(TG_OP,1,1));
		RETURN OLD;
	else
		RAISE WARNING '[movie_sync_function] - unknown action occurred: %, at %', TG_OP, now();
		RETURN NULL;
	end if;
END;
$$;;

CREATE TRIGGER movie_sync_trigger
	AFTER INSERT OR UPDATE OR DELETE
	ON movie
	FOR EACH ROW
	EXECUTE PROCEDURE movie_sync_function();;
