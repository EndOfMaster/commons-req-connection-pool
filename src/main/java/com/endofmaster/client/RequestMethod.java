package com.endofmaster.client;

/**
 * @author ZM.Wang
 */
public enum RequestMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT");

    private final String name;

    RequestMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }}
