package com.endofmaster.client;

import com.endofmaster.client.exception.RequestClientException;
import com.endofmaster.client.exception.RequestServerException;
import com.endofmaster.commons.util.StreamUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author ZM.Wang
 */
public class BaseHttpResponse {

    private int statusCode;
    private String reasonPhrase;
    private String contentType;
    private InputStream body;

    BaseHttpResponse(HttpResponse httpResponse) throws IOException {
        this.statusCode = httpResponse.getStatusLine().getStatusCode();
        this.reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
        this.body = httpResponse.getEntity().getContent();
//        String s = StreamUtils.copyToString(body, Charset.forName("UTF-8"));
//        System.err.println(s);
        this.contentType = httpResponse.getEntity().getContentType().getValue();
    }

    public <T extends BasicResponse> T parse(Class<T> tClass) throws RequestClientException {
        try {
            if (statusCode >= 200 && statusCode < 300) {
                return RequestHttpClient.MAPPER.readValue(body, tClass);
            } else {
                throw new RequestServerException("Failed to parse body, invalid status code");
            }
        } catch (IOException e) {
            throw new RequestClientException(e.getLocalizedMessage(), e);
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getBody() {
        return body;
    }
}
