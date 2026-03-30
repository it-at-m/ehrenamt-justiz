# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi10/nginx-126:10.1-1774874187@sha256:38f52aaef9561380415885eb136274bdaf88879458c4680cf19033cc885a30ca

# Copy built online
COPY dist/ /opt/app-root/src/public/online/

# Copy custom nginx configurations
COPY docker/nginx/*.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD ["nginx", "-g", "daemon off;"]