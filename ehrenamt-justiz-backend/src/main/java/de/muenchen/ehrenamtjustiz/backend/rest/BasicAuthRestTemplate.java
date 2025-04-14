package de.muenchen.ehrenamtjustiz.backend.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public final class BasicAuthRestTemplate extends RestTemplate {

    public BasicAuthRestTemplate(final String username, final String password) { //NOPMD
        addAuthentication(username, password);
    }

    private void addAuthentication(final String username, final String password) {
        if (username == null) {
            return;
        }
        final List<ClientHttpRequestInterceptor> interceptors = Collections
                .singletonList(
                        new BasicAuthorizationInterceptor(username, password));
        setRequestFactory(new InterceptingClientHttpRequestFactory(getRequestFactory(),
                interceptors));
    }

    private static class BasicAuthorizationInterceptor implements
            ClientHttpRequestInterceptor {

        private final String username;

        private final String password;

        public BasicAuthorizationInterceptor(final String username, final String password) {
            this.username = username;
            this.password = (password == null ? "" : password);
        }

        @Override
        public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                final ClientHttpRequestExecution execution) throws IOException {
            final byte[] token = Base64.getEncoder().encode(
                    (this.username + ":" + this.password).getBytes(StandardCharsets.UTF_8));
            request.getHeaders().add("Authorization", "Basic " + new String(token, StandardCharsets.UTF_8));
            return execution.execute(request, body);
        }

    }

}
