# Note: For base images an architecture-agnostic SHA is always preferred, see https://refarch.oss.muenchen.de/templates/deploy.html#docker-images for more info.

# For documentation see https://rh-openjdk.github.io/redhat-openjdk-containers/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-14@sha256:53fc57721acb1941bef7fcd400e35353dace05087309ff482bb6814714d3095d

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar