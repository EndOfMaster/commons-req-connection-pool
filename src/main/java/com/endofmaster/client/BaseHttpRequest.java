package com.endofmaster.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZM.Wang
 */
public class BaseHttpRequest {

    private final List<Arg> args = new ArrayList<>();
    private final Map<String, String> headers = new LinkedHashMap<>();

    private RequestMethod method = RequestMethod.GET;
    private RequestContentType dataType = RequestContentType.JSON;
    private String url;

    public BaseHttpRequest(String url) {
        this.url = url;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public BaseHttpRequest withHeader(String name, String value) {
        setHeader(name, value);
        return this;
    }

    public BaseHttpRequest withArg(String key, Object value) {
        if (value != null) {
            args.add(new Arg(key, value));
        }
        return this;
    }

    /**
     * Unlike {@link #withArg(String, Object)}, overrides the existing value
     */
    public BaseHttpRequest setArg(String key, Object value) {
        for (Arg e : args) {
            if (e.key.equals(key)) {
                e.value = value;
                return this;
            }
        }
        return withArg(key, value);
    }

    public BaseHttpRequest setMethod(RequestMethod method) {
        this.method = method;
        return this;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public RequestContentType getDataType() {
        return dataType;
    }

    public BaseHttpRequest setDataType(RequestContentType dataType) {
        this.dataType = dataType;
        return this;
    }

    public BaseHttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<Arg> getArgs() {
        return Collections.unmodifiableList(args);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getUrl() {
        return url;
    }

    public static class Arg {
        String key;
        Object value;

        private Arg(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
