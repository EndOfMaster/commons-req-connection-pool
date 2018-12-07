package com.endofmaster.client;

import com.endofmaster.client.exception.RequestClientException;
import com.endofmaster.commons.util.StreamUtils;
import com.endofmaster.commons.util.p12cert.P12Cert;
import com.endofmaster.commons.util.p12cert.P12CertUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.MULTIPART_FORM_DATA;

/**
 * @author ZM.Wang
 */
public class RequestHttpClient {

    static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setVisibility(new VisibilityChecker.Std(NONE, NONE, NONE, NONE, ANY));
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final HttpClient httpClient;

    public RequestHttpClient() {
        this(null, null);
    }

    public RequestHttpClient(final InputStream cert, final String certPassword) {
        this(5000, 5000, cert, certPassword);
    }

    public RequestHttpClient(final int connectTimeout, final int socketTimeout, final InputStream cert, final String certPassword) {
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout)
                    .build();
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            if (cert != null) {
                SSLConnectionSocketFactory sslSocketFactory = buildSsl(cert, certPassword);
                httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
            }
            httpClient = httpClientBuilder.setDefaultRequestConfig(config).build();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RequestClientException(e.getLocalizedMessage(), e);
        }
    }

    public BaseHttpResponse execute(BaseHttpRequest baseHttpRequest) throws RequestClientException {
        HttpUriRequest httpRequest = buildHttpUriRequest(baseHttpRequest);
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            return new BaseHttpResponse(httpResponse);
        } catch (IOException e) {
            throw new RequestClientException(e.getLocalizedMessage(), e);
        }
    }

    private HttpUriRequest buildHttpUriRequest(BaseHttpRequest wxHttpRequest) throws RequestClientException {
        RequestBuilder requestBuilder = RequestBuilder.create(wxHttpRequest.getMethod().getName())
                .setUri(wxHttpRequest.getUrl());

        for (String headerKey : wxHttpRequest.getHeaders().keySet()) {
            requestBuilder.addHeader(headerKey, wxHttpRequest.getHeaders().get(headerKey));
        }

        if (RequestMethod.POST == wxHttpRequest.getMethod() || RequestMethod.PUT == wxHttpRequest.getMethod()) {
            Map<String, Object> map = new HashMap<>();
            for (BaseHttpRequest.Arg arg : wxHttpRequest.getArgs()) {
                map.put(arg.key, arg.value);
            }
            if (RequestContentType.JSON == wxHttpRequest.getDataType()) {
                try {
                    String json = MAPPER.writeValueAsString(map);
                    StringEntity entity = new StringEntity(json, APPLICATION_JSON);
                    requestBuilder.setEntity(entity);
                } catch (JsonProcessingException e) {
                    throw new RequestClientException(e.getLocalizedMessage(), e);
                }
            } else {
                try {
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    for (String key : map.keySet()) {
                        Object value = map.get(key);
                        if (value instanceof String) {
                            builder.addTextBody(key, value.toString());
                        } else {
                            byte[] data = StreamUtils.copyToByteArray((InputStream) value);
                            builder.addBinaryBody(key, data, MULTIPART_FORM_DATA, key);
                        }
                    }
                    builder.setMode(HttpMultipartMode.RFC6532);
                    requestBuilder.setEntity(builder.build());
                } catch (IOException e) {
                    throw new RequestClientException(e.getLocalizedMessage(), e);
                }
            }
        } else {
            for (BaseHttpRequest.Arg arg : wxHttpRequest.getArgs()) {
                requestBuilder.addParameter(arg.key, arg.value.toString());
            }
        }
        return requestBuilder.build();
    }

    private SSLConnectionSocketFactory buildSsl(InputStream cert, String certPassword) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        P12Cert p12Cert = P12CertUtils.load(cert, certPassword.toCharArray());
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(p12Cert.getKeyStore(), certPassword.toCharArray()).build();
        // Allow TLSv1 protocol only
        return new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
    }
}
