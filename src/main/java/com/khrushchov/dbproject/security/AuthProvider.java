package com.khrushchov.dbproject.security;

import com.khrushchov.dbproject.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthProvider implements AuthenticationProvider {
    @Autowired private SecurityUserDetailsService userDetailsService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Employee employee = (Employee) userDetailsService.loadUserByUsername(username);

        if (username.equals(employee.getUsername()) && passwordEncoder.matches(password, employee.getPassword())) {
            return new UsernamePasswordAuthenticationToken(username, passwordEncoder.encode(password), List.of(employee::getRole));
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
