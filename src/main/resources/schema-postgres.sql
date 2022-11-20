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
