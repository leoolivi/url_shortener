package com.urlshortener.data.request.keys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdatedKeyEvent implements KeyEvent {
    private String publicKey;   
}
