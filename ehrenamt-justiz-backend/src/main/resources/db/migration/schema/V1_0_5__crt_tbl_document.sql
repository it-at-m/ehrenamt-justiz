-- SQL-Skript PostgreSQL-DB (04/2026):
-- 1. Schema-Praefix(?)
-- 2. Table space(?)
-- 3. owner2tbl
--

--------------------------------------------------------------------------------
--  DDL for Table DOCUMENT
--------------------------------------------------------------------------------

drop table if exists document cascade;

create table if not exists document (
   id uuid primary key,
   content_type character varying(20) not null,
   file_id character varying(51),
   file_length bigint,
   file_name character varying(2000) not null,
   file_data bytea not null,
   person_id uuid,
   document_source character varying(20) not null,
   constraint file_name_person_id_uk UNIQUE (file_name, person_id)
);