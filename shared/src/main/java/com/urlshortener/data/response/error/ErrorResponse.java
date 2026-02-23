package com.urlshortener.data.response.error;

import com.urlshortener.data.Response;

public record ErrorResponse (
    String error,
    String message
) implements Response {
}
