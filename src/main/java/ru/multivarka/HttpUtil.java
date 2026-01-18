package ru.multivarka;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public final class HttpUtil {
    private HttpUtil() {
    }

    public static HttpResult get(String url, Map<String, String> headers, int timeoutMs) throws Exception {
        return execute("GET", url, null, headers, timeoutMs);
    }

    public static HttpResult post(String url, String body, Map<String, String> headers, int timeoutMs) throws Exception {
        return execute("POST", url, body, headers, timeoutMs);
    }

    private static HttpResult execute(String method, String url, String body, Map<String, String> headers, int timeoutMs)
            throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (body != null) {
            connection.setDoOutput(true);
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            try (OutputStream output = connection.getOutputStream()) {
                output.write(bytes);
            }
        }
        int status = connection.getResponseCode();
        InputStream stream = status >= 200 && status < 400 ? connection.getInputStream() : connection.getErrorStream();
        String responseBody = "";
        if (stream != null) {
            responseBody = readAll(stream);
        }
        connection.disconnect();
        return new HttpResult(status, responseBody, connection.getHeaderFields());
    }

    private static String readAll(InputStream stream) throws Exception {
        try (BufferedInputStream input = new BufferedInputStream(stream);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    public static class HttpResult {
        private final int statusCode;
        private final String body;
        private final Map<String, java.util.List<String>> headers;

        public HttpResult(int statusCode, String body, Map<String, java.util.List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers == null ? Collections.<String, java.util.List<String>>emptyMap() : headers;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }

        public Map<String, java.util.List<String>> getHeaders() {
            return headers;
        }
    }
}
