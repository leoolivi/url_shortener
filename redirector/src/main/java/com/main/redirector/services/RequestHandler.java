package com.main.redirector.services;

import com.urlshortener.data.Request;
import com.urlshortener.data.Response;

public interface RequestHandler {
    public Class<?> getRequestType();
    public String getRoutingKey();
    public Response handleRequest(Request request);
}
