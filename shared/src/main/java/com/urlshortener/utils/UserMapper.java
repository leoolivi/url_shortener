package com.urlshortener.utils;

import com.urlshortener.data.response.user.UserResponse;
import com.urlshortener.data.user.AuthenticatedUser;
import com.urlshortener.data.user.UserClaims;

public class UserMapper {
    public AuthenticatedUser fromUserResponseToAuthenticated(UserResponse response) {
        return new AuthenticatedUser(response.getId(), response.getEmail(), response.getRole());
    }

    public UserClaims fromUserResponseToClaims(UserResponse response) {
        return new UserClaims(response.getId(), response.getEmail(), response.getRole(), null);
    }
}


