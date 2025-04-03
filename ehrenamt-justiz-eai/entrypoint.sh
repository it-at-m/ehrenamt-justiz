#!/bin/bash
# This script is executed in the Docker container and has the following tasks:

# - Determine certificates using the curl command and save them under /tmp/..., for example:
#     e.g. curl -o /tmp/LHM-....pem http://.../LHM-....pem
#     ATTENTION: The necessary certificates must be added manually before starting this script!
#     The full script can be found in KeyPass for Ehrenamtjustiz
#
echo "Download certificates"

# - Importing the certificates using the keytool command
#     e.g. keytool -import -noprompt -keystore /tmp/cacerts-lhm -file /tmp/LHM-....pem -storepass changeit -alias basename--LHM-....pem
#     ATTENTION: The necessary certificates must be added manually before starting this script!
#     The full script can be found in KeyPass for Ehrenamtjustiz
#
echo "Keytool: import of certificates"

# - Starting the EAI with the “docker” profile
#     ATTENTION: The trustStorePassword has to be changed manually!
echo "start EAI"
exec java -Dspring.profiles.active=docker -Djavax.net.ssl.trustStore=/tmp/cacerts-lhm -Djavax.net.ssl.trustStorePassword=... -jar /deployments/application.jar