# Note: For base images an architecture-agnostic SHA is always preferred, see https://refarch.oss.muenchen.de/templates/deploy.html#docker-images for more info.

# For documentation see https://rh-openjdk.github.io/redhat-openjdk-containers/
FROM registry.access.redhat.com/ubi10/openjdk-25-runtime:1.24-16@sha256:5161ac11d66c3c998387192d7f6fc22ac7d25792a172eb0b93b718d0ef38cc65

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar