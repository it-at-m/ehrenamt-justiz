# Residents registration change service  of Ehrenamt-Jusitz

The Residents registration (EWO) offers a job for push notifications that ensures that change notifications are sent smoothly and regularly to Ehrenamt-Jusitz.
The changes from EWO are transmitted to us via Apcache Kafka queues. The Residents registration change service receives changed personal data and and determines conflicts (Conflicts arise if the applicant data in Ehrenamt-Jusitz is different from the Residents registration data)

## Konfiguration of Kafka service
To retrieve the data from apache kafka service, the following settings must be made:

application-[profile].yml
```
aenderungsservice:
...
  topicPattern: lhm-ewo-eai-aenderungen-.*
  group-Id: lhm-ewo-eai-aenderungen-ehrenamt-justiz
  ```

| Konfiguration | Description |
| ------------- | ------------- |
| topicPattern  | Kafka-Topics. Has to be 'lhm-ewo-eai-aenderungen-.*' |
| group-Id | Identifies a group of consumer instances that jointly consume messages from one or more Kafka topics. Has to be 'lhm-ewo-eai-aenderungen-ehrenamt-justiz' |

The following configurations must be carried out so that the change service can call up the backend

application-[profile].yml
```
aenderungsservice:
  backend:
    server: http://localhost:8083
    base-path: /public/aenderungsservice
    connecttimeout: 30000
    readtimeout: 30000
    retry:
      maxRetries: 30
      initialInterval: 5000
      multiplier: 2.0
      maxInterval: 80000
  ```
| Konfiguration | Description |
| ------------- | ------------- |
| server  | Host server of backend. For example 'http://localhost:8083' |
| base-path | Has to be '/public/aenderungsservice' |
| connecttimeout | Timeout when calling the backend. In milliseconds |
| readtimeout | Readtimeout when calling the backend. In milliseconds |
| maxRetries | Configuration is used, if blocking entry. Maximum number of calls if, for example, a timeout occurs when calling up the backend |
| initialInterval | Configuration is used, if blocking entry. Waiting time after the first incorrect call of the backend |
| multiplier | Configuration is used, if blocking entry. Multiplier for the waiting time after the first faulty call of the backend |
| maxInterval | Configuration is used, if blocking entry. Maximum waiting time  |

A “blocking entry” in Kafka listener contexts refers to a situation where a consumer (in this case a Kafka listener) is blocked from processing messages and is unable to consume new messages from a Kafka partition until the current processing is complete (For example, timeout when calling the backend or when calling the Residents registration EAI).
