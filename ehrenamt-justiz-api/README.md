# Ehrenamt-Justiz API

The Ehrenamt-Justiz API defines the interface between the core application of Ehrenamt-Justiz and the EAI for the residents' registration system

Structure for search request to EWO-EAI (BuergerSucheAnfrage):

| name           | datatype  |  description                     |
|:---------------|:-------------------|:------------------------|
| familienname   | string    | surname                          |
| vorname        | string    | given name                       |

Structure for response from EWO-EAI (EWOBuerger.):

| name                   | datatype  |  description                     |
|:-----------------------|:----------|:---------------------------------|
| familienname  string    | surname |
| familiennameZusatz | string    | surname addition  |
| geburtsname | string    | birth name |
| geburtsnameZusatz | string    | birth name addition |
| vorname | string    | given name |
| geburtsdatum | LocalDate | date of birth |
| geschlecht | enum: MAENNLICH, WEIBLICH, DIVERS or UNBEKANNT | gender (male, female, diverse or unknown) |
| ordnungsmerkmal | string | id in resident registration|
| geburtsort | string | place of birth |
| geburtsland | string | country of birth |
| familienstand | string | marital status |
| staatsangehoerigkeit | string | nationality |
| wohnungsgeber | string | housing provider |
| strasse | string | street |
| hausnummer | string | street number |
| appartmentnummer | string | apartment number |
| buchstabeHausnummer| string | letter of house number |
| stockwerk | string | number of floor |
| teilnummerHausnummer| string | partial number of the house number |
| zusatz| string | addition |
| konfliktFelder | List of String | data fields with conflicts |
| postleitzahl | string | postal code |
| ort | string | place of residence |
| inMuenchenSeit| LocalDate | in munich since |
| wohnungsstatus | enum: HAUPTWOHNUNG or NEBENWOHNUNG | Residential status: main residence or secondary residence |
| auskunftssperren | List of String | blocking of information if list is not empty|


The release is built by manually calling the action “api-release” (https://github.com/it-at-m/ehrenamt-justiz/actions/workflows/api-release.yml) and saved in the central Maven repository (https://central.sonatype.com/search?q=ehrenamt-justiz).

