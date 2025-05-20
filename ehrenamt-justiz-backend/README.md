# Database schema
## ER-Diagramm
The following figure shows the ER diagram with all tables and the relationships between them.
![ER Model](../docs/images/DB_ER_Model.png)


## Tables in Database schema
The database schema currently contains 6 tables. The following table lists all the DB tables contained in the DB schema with their description.

| Table                    | Description                                                                     | 
|:-------------------------|:--------------------------------------------------------------------------------|
| konfiguration            | Configuration values (e.g. term of office from-to)                              |
| person                   | Personal data (EWO and additional data) for applicants, conflicts and proposals | 
| staatsangehoerigkeit     | Contains the nationalities of an applicant                                      | 
| konfliktfeld             | Contains the conflict fields (changes to EWO)                                   | 
| auskunftssperre          | This table provides information on information blocks                           |
| flyway_schema_history    | Technical change history of the database (Flyway)                               |

## Database server
The PostgreSQL database servers requested for the individual environments in the MIA server network segment are listed in the following table.

| Server (:Port)                         | Environment | OS       | Description/DB schema                              |
|:---------------------------------------|:------------|:---------|:---------------------------------------------------|
| not yet applied for                    | Production  | RHEL 8   | PostgreSQL-Database server ehrju (PostgreSQL 16.6) |
| ehrjudpk001.srv.muenchen.de:5432       | Testing     | RHEL 8   | PostgreSQL-Database server ehrju (PostgreSQL 16.6) |
| ehrjudpc001.srv.muenchen.de:5432       | Coding      | RHEL 8   | PostgreSQL-Datenbankserver ehrju (PostgreSQL 16.6) |

### SQL-Commands 
The following SQL command can be used to link all tables for an evaluation, for example:    
```
select p.vorname, p.familienname, k.bezeichnung, s.staatsangehoerigkeit_text, f.person_attribut, a.sperrentyp from ehrju.person p
left outer join ehrju.konfiguration k on k.id = p.konfiguration_id
left outer join ehrju.staatsangehoerigkeit s on s.person_id = p.id
left outer join ehrju.konfliktfeld f on f.person_id = p.id
left outer join ehrju.auskunftssperre a on a.person_id = p.id
order by p.familienname asc
```

### Changes to the database schema
Changes to the database schema are made using the Flyway framework (https://www.red-gate.com/products/flyway/).
Changes to the database schema are made here:
![Change of database schema](/ehrenamt-justiz-backend/src/main/resources/db/migration/schema)
Only new files may be created here. Existing files may not be changed.

Important Maven commands in connection with Flyway:
| Command                  | Description                                                 | 
|:-------------------------|:------------------------------------------------------------|
| mvn flyway:info          | Get information about current database                      |
| mvn flyway:repair        | Corrects an incorrect migration. After correcting the migration SQL commands, execute the migration again  |
