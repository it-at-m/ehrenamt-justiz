# Residents registration EAI (Enterprise application integration)

The Residents registration EAI has the task of determining personal data from the residents' registration system (e.g. first name, surname, address). The EAI is used in the core application, the online application and the residents registration change service. This EAI is used to decouple the interface to the residents' registration system from the application Ehrenamt-Jusitz. In its current state, the interface provides a connection to the EWO system of the AKDB (Anstalt für Kommunale Datenverarbeitung in Bayern). If a different connection is required for a different system, this EAI must be adapted. Two requests are supported:

| Requestname | Params | Response |
| ------------- | ------------- | ------------- |
| ewosuche  | vorname (first name), nachname (surname), geburtsdatum (date of birth) | personal data e.g. adress |
| ewosuchemitom | OM (id of personal data) | personal data e.g. first name, surname and adress |



The interface is described in detail here in the wsdl: 
[ewoeai.wsdl](https://github.com/it-at-m/ehrenamt-justiz/blob/main/ehrenamt-justiz-eai/src/main/resources/wsdl/ewoeai.wsdl)

## Configuration

Configuration of this EAI in application-[profile].yml:

| Configuration | Description | Default setting |
| ------------- | ------------- | ------------- |
| ewo.eai.url | url of residents' registration system | http://.... |
| producer.user | User for authentication on residents' registration system |  |
| producer.password | Password for authentication on residents' registration system |  |
| api.auth.users.username | User for authentication on this EAI |  |
| api.auth.users.password | Password for authentication on this EAI |  |

