-- (c) LH Muenchen, Ehrenamtjustiz
-- Ersteller: Udo Brandes
--
-- SQL-Skript PostgreSQL-DB (09.01.2025):
-- 1. Schema-Praefix(?)
-- 2. Table space(?)
-- 3. owner2tbl
--

create unique index Person_Familienname_Vorname_Id_uk
    on person (familienname, vorname, id);