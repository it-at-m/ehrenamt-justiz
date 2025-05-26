# Docker-Compose

| Image/Container | Description | Port | Link |
| ------------- | ------------- | ------------- |  ------------- |
| postgres | PostgreSQL | 5432 | 
| pg-admin | Administration and development platform for PostgreSQL | 5050 | http://localhost:5050 |
| init-keycloak | Initializes client, roles, user .. for keycloak  |  | |
| keycloak |  Identity and Access Management | 8100 |  http://localhost:8100 |
| refarch-gateway | A Spring-Cloud-Gateway | 8083 |  http://localhost:8083 |
| appswitcher-server | Provides a component to quickly switch between apps provided by an appswitcher-server instance | 8084 |  http://localhost:8084 |
| backend | Backend logic | 39146 |
| frontend | Core application for Ehrenamt-Jusitz | 8081 | http://localhost:8083 |
| online | Citizens can use the online application to apply for the office of lay judge | 8082 | http://localhost:8083/public/online/ |
| eai | Residents registration EAI (Enterprise application integration) | 8085 | |
| aenderungsservice | Residents registration change service of Ehrenamt-Justiz | 8086 | |

Start of docker-compose:
````
cd ~/develop/ehrenamt-justiz/stack/
docker-compose --profile=backend --profile=frontend --profile=online --profile=eai --profile=aenderungsservice up -d
````

Stop of docker-compose:
````
cd ~/develop/ehrenamt-justiz/stack/
docker-compose --profile=backend --profile=frontend --profile=online --profile=eai --profile=aenderungsservice stop -d
````

