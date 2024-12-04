package com.bitescout.app.authservice.security;

import com.bitescout.app.authservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    /**
     * This method checks if the provided authentication class is assignable from
     * UsernamePasswordAuthenticationToken. This ensures that this AuthenticationProvider
     * only handles authentication requests that are instances of UsernamePasswordAuthenticationToken
     * or its subclasses.
     *
     * @param authentication the class of the authentication object
     * @return true if the authentication class is assignable from UsernamePasswordAuthenticationToken, false otherwise
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}