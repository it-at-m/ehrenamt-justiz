package de.muenchen.ehrenamtjustiz.eai.personeninfo.filter;

import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for Logging requests and responses
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class RequestResponseLoggingFilter implements Filter {
    private String charsetName;
    private int bufferSize = 1024;
    private final Logger log;

    public RequestResponseLoggingFilter() {
        this.log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    }

    public RequestResponseLoggingFilter(final String loggerName) {
        this.log = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final long startTimeInMillis = System.currentTimeMillis();
        final HttpServletResponse res = (HttpServletResponse) response;
        final RequestWrapper req = new RequestWrapper((HttpServletRequest) request);
        final BufferedResponseWrapper resWrap = new BufferedResponseWrapper(res);
        this.log.info("Inbound Message:\nRequestUrl: {}\nCharacter-Encoding: {}\nContent-Type: {}\nMethod: {}\nRemoteHost: {}\nHeaders: {}\nPayload: {}",
                req.getRequestURL(), req.getCharacterEncoding(), req.getContentType(), req.getMethod(), req.getRemoteHost(),
                this.getHeaders(req), req.getBody());
        chain.doFilter(req, resWrap);
        final long endTimeInMillis = System.currentTimeMillis();
        final long timeTaken = endTimeInMillis - startTimeInMillis;
        this.log.info("Outbound Message:\nTime taken: {}\nHttp-Status: {}\nCharacter-Encoding: {}\nContent-Type: {}\nHeaders: {}\nPayload: {}",
                timeTaken, res.getStatus(), res.getCharacterEncoding(), res.getContentType(), this.getHeaders(res), resWrap.getContent());
    }

    private Map<String, String> getHeaders(final HttpServletRequest request) {
        final Map<String, String> headers = new HashMap<>();
        final Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                final String headername = headerNames.nextElement();
                headers.put(headername, StringUtils.join(this.toList(request.getHeaders(headername))));
            }
        }

        return headers;
    }

    private Map<String, String> getHeaders(final HttpServletResponse response) {
        final Map<String, String> headers = Maps.newHashMapWithExpectedSize(response.getHeaderNames().size());

        for (final String headername : response.getHeaderNames()) {
            headers.put(headername, StringUtils.join(response.getHeaders(headername)));
        }

        return headers;
    }

    private List<String> toList(final Enumeration<String> enumeration) {
        final List<String> list = new LinkedList<>();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                list.add(enumeration.nextElement());
            }
        }

        return list;
    }

    @Override
    public void destroy() {
    }

    public RequestResponseLoggingFilter(final Logger log) {
        this.log = log;
    }

    public String getCharsetName() {
        return this.charsetName;
    }

    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    private static class TeeServletOutputStream extends ServletOutputStream {
        private final TeeOutputStream targetStream;

        public TeeServletOutputStream(final OutputStream one, final OutputStream two) {
            super();
            this.targetStream = new TeeOutputStream(one, two);
        }

        @Override
        public void write(final int arg0) throws IOException {
            this.targetStream.write(arg0);
        }

        @Override
        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.targetStream.close();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    @SuppressWarnings("PMD.AssignmentInOperand")
    public static class RequestWrapper extends HttpServletRequestWrapper {
        private final String body;

        public RequestWrapper(final HttpServletRequest request) throws IOException {
            super(request);
            final StringBuilder stringBuilder = new StringBuilder();

            try (InputStream inputStream = request.getInputStream()) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    final char[] charBuffer = new char[128];

                    int bytesRead;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                }
            }

            this.body = stringBuilder.toString();
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body.getBytes(StandardCharsets.UTF_8));
            return new ServletInputStream() {

                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return true;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener rl) {
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
        }

        public String getBody() {
            return this.body;
        }
    }

    private class BufferedResponseWrapper extends HttpServletResponseWrapper {
        private final HttpServletResponse original;
        private TeeServletOutputStream tee;
        private ByteArrayOutputStream bos;

        public BufferedResponseWrapper(final HttpServletResponse response) {
            super(response);
            this.original = response;
        }

        public String getContent() throws UnsupportedEncodingException {
            return this.bos == null ? null : this.bos.toString(RequestResponseLoggingFilter.this.charsetName);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return this.original.getWriter();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (this.tee == null) {
                this.bos = new ByteArrayOutputStream();
                this.tee = new TeeServletOutputStream(this.original.getOutputStream(), this.bos);
            }

            return this.tee;
        }
    }
}
