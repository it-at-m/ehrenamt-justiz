# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.0-1755779634@sha256:6f0065932514e2c699a17befb46f25e74d7544249bd5dffa70a86b03cd44921a

# Copy built online
COPY dist/ /opt/app-root/src/public/online/

# Copy custom nginx configurations
COPY docker/nginx/*.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]