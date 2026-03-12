package com.main.gateway.services;

import org.springframework.stereotype.Service;

import com.urlshortener.security.keys.StringKeyHolder;

@Service
public class InternalKeyHolder extends StringKeyHolder {}
