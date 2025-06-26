-- Alter table Person
-- ========================
--
-- Alter length of column familienname and geburtsname of table Person
ALTER TABLE Person alter column familienname TYPE varchar(300);
ALTER TABLE Person alter column geburtsname TYPE varchar(300);
