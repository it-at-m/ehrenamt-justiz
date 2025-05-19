# Residents registration EAI (Enterprise application integration)

The Residents registration EAI has the task of determining personal data from the residents' registration system (e.g. first name, surname, address). Two requests are supported:

| Requestname | Params | Response |
| ------------- | ------------- | ------------- |
| ewosuche  | vorname (first name), nachname (surname), geburtsdatum (date of birth) | personal data e.g. adress |
| ewosuchemitom | OM (id of personal data) | personal data e.g. adress |

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

