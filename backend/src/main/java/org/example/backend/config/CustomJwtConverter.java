package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.example.backend.services.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final UserService userService;
    private final JwtAuthenticationConverter defaultConverter = new JwtAuthenticationConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        userService.syncUserFromJwt(jwt);

        return defaultConverter.convert(jwt);
    }

}
