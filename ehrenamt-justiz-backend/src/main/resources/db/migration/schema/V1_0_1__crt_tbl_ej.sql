-- (c) LH Muenchen, Ehrenamtjustiz
-- Ersteller: Udo Brandes
--
-- SQL-Skript PostgreSQL-DB (19.04.2024):
-- 1. Schema-Praefix(?)
-- 2. Table space(?)
-- 3. owner2tbl
--

--------------------------------------------------------------------------------
--  DDL for Table KONFIGURATION
--------------------------------------------------------------------------------

drop table if exists Konfiguration cascade;

create table if not exists Konfiguration (
    id uuid primary key,
    ehrenamtjustizart varchar(255),
    bezeichnung varchar(255),
    aktiv boolean not null,
    altervon numeric not null,
    alterbis numeric not null,
    amtsperiodevon date not null,
    amtsperiodebis date not null,
    staatsangehoerigkeit character varying(255) not null,
    wohnsitz character varying(255) not null
);

--------------------------------------------------------------------------------
--  DDL for Table PERSON
--------------------------------------------------------------------------------

drop table if exists Person cascade;

create table if not exists Person (
    id uuid primary key,
    ewoid varchar(255),
    konfigurationid uuid,
    familienname varchar(255) not null,
    geburtsname varchar(255),
    vorname varchar(255) not null,
    geburtsdatum date not null,
    geschlecht varchar(255) not null,
    akademischergrad varchar(255),
    geburtsort varchar(255) not null,
    geburtsland varchar(255) not null,
    familienstand varchar(255) not null,
    strasse varchar(255) not null,
    hausnummer varchar(255) not null,
    appartmentnummer varchar(255),
    buchstabehausnummer varchar(255),
    stockwerk varchar(255),
    teilnummerhausnummer varchar(255),
    adresszusatz varchar(255),
    postleitzahl varchar(255) not null,
    ort varchar(255) not null,
    inmuenchenseit date not null,
    wohnungsgeber varchar(255),
    wohnungsstatus varchar(255) not null,
    derzeitausgeuebterberuf varchar(255),
    arbeitgeber varchar(255),
    telefonnummer varchar(255),
    telefongesch varchar(255),
    telefonmobil varchar(255),
    mailadresse varchar(150),
    ausgeuebteehrenaemter varchar(4000),
    onlinebewerbung boolean,
    neuervorschlag boolean,
    warbereitstaetigals boolean,
    bewerbungvom date,
    status varchar(255),
    bemerkung varchar(255)
);

alter table Person add constraint uk_person_konfig_ewo_id unique (ewoid, konfigurationid);
alter table Person add constraint fk_person_konfiguration_id foreign key (konfigurationid) REFERENCES Konfiguration (id);

--------------------------------------------------------------------------------
--  DDL for Table Auskunftssperre
--------------------------------------------------------------------------------

create table if not exists  Auskunftssperre (
    personid uuid not null,
    sperrentyp varchar(255),
    lfdnr integer not null,
    primary key (personid, lfdnr)
);

alter table Auskunftssperre add constraint fk_auskunftssperre_person_id foreign key (personid) references  Person(id);

--------------------------------------------------------------------------------
--  DDL for Table Konfliktfeld
--------------------------------------------------------------------------------

create table if not exists  Konfliktfeld (
    personid uuid not null,
    person_attribut varchar(255),
    lfdnr integer not null,
    primary key (personid, lfdnr)
);

alter table Konfliktfeld add constraint fk_konfliktfeld_person_id foreign key (personid) references  Person(id);

--------------------------------------------------------------------------------
--  DDL for Table Staatsangehoerigkeit
--------------------------------------------------------------------------------

create table if not exists  Staatsangehoerigkeit (
    personid uuid not null,
    staatsangehoerigkeit_text varchar(255),
    lfdnr integer not null,
    primary key (personid, lfdnr)
);

alter table Staatsangehoerigkeit add constraint fk_staatsangehoerigkeit_person_id foreign key (personid) references Person(id);


