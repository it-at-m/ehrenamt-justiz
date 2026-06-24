# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.2-1782247488@sha256:72bfdc186993361ce711b11c59f079f93248ce1e27c11b0c767b54ac3191a787

# Copy built web application
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/*.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]