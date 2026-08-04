package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.example.backend.services.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final UserService userService;
    private final JwtAuthenticationConverter defaultConverter;

    public CustomJwtConverter(UserService userService) {
        this.userService = userService;
        this.defaultConverter = new JwtAuthenticationConverter();
        this.defaultConverter.setJwtGrantedAuthoritiesConverter(this::extractKeycloakRoles);
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        userService.syncUserFromJwt(jwt);

        return defaultConverter.convert(jwt);
    }

    private Collection<GrantedAuthority> extractKeycloakRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Collections.emptyList();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

}
