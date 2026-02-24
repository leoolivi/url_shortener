package com.main.redirector.services;

import org.springframework.stereotype.Component;

import com.urlshortener.data.Request;
import com.urlshortener.data.Response;
import com.urlshortener.data.request.redirect.RedirectRequest;
import com.urlshortener.data.response.redirect.RedirectResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class RedirectRequestHandler implements RequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestType() {
        return RedirectRequest.class;
    }

    @Override
    public Response handleRequest(Request request) {
        RedirectRequest reqPayload = objectMapper.convertValue(request, RedirectRequest.class);
        RedirectResponse res = new RedirectResponse(
                                        reqPayload.code(),
                                        urlMappingService.getOriginalUrl(reqPayload.code()));
        return res;
    }

    @Override
    public String getRoutingKey() {
        return "mapping.redirect";
    }
    
}
