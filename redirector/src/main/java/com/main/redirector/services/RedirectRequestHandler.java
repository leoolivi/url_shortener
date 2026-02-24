package com.main.redirector.services;

import org.springframework.stereotype.Component;

import com.main.redirector.exceptions.MappingNotFoundException;
import com.urlshortener.data.Request;
import com.urlshortener.data.Response;
import com.urlshortener.data.request.redirect.RedirectRequest;
import com.urlshortener.data.response.error.ErrorResponse;
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
        Response res;
        try {
            String originalUrl = urlMappingService.getOriginalUrl(reqPayload.code());
            res = new RedirectResponse(
                                            reqPayload.code(),
                                            originalUrl);
        } catch (MappingNotFoundException e) {
            res = new ErrorResponse("REDIRECT_NOT_FOUND", "Redirect not found for code="+reqPayload.code());
        }
        return res;
    }

    @Override
    public String getRoutingKey() {
        return "mapping.redirect";
    }
    
}
