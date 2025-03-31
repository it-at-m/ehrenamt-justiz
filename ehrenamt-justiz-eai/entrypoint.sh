#!/bin/bash

# Die folgenden Zeilen sind notwendig, damit aus Docker heraus zugegriffen werden kann:
echo "Zertifikate herunterladen"


echo "Keytool: import von Zertifikaten"


echo "start EAI"
exec java -Dspring.profiles.active=docker -Djavax.net.ssl.trustStore=/tmp/cacerts-lhm -Djavax.net.ssl.trustStorePassword=changeit -jar /deployments/application.jar
