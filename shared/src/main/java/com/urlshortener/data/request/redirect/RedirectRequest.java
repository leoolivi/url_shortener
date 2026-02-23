package com.urlshortener.data.request.redirect;

import com.urlshortener.data.Request;

public record RedirectRequest (
    String code
) implements Request {}
