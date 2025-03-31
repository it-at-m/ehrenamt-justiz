#!/bin/bash
# Dieses Script wird im Container von Docker ausgeführt und hat folgende Aufgaben:
#
# - Zertiifikate mit Hilfe des curl-Befehls ermitteln und z.B. unter /tmp/.. speichern
#     z.B. curl -o /tmp/LHM-....pem http://.../LHM-....pem
#     ACHTUNG: Die notwendigen Zertifikate müssen vor dem Start dieses Scriptes manuell ergänzt werden!!
#
# - Importieren der Zertifikate mit Hilfe des keytool-Befehls
#     z.B. keytool -import -noprompt -keystore /tmp/cacerts-lhm -file /tmp/LHM-....pem -storepass changeit -alias basename--LHM-....pem
#     ACHTUNG: Die notwendigen Zertifikate müssen vor dem Start dieses Scriptes manuell ergänzt werden!!
#
# - Starten der EAI mit dem Profil "docker"

echo "Zertifikate herunterladen"


echo "Keytool: import von Zertifikaten"

echo "start EAI"
# ACHTUNG: Password hier ergänzen
exec java -Dspring.profiles.active=docker -Djavax.net.ssl.trustStore=/tmp/cacerts-lhm -Djavax.net.ssl.trustStorePassword=.... -jar /deployments/application.jar