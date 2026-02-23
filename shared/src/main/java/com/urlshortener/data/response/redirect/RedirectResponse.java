package com.urlshortener.data.response.redirect;

import com.urlshortener.data.Response;

public record RedirectResponse(
    String code,
    String originalUrl
) implements Response {}
