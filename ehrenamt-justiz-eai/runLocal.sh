#!/bin/bash
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=env-local"
