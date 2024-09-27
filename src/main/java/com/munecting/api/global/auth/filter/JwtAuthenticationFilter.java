package com.munecting.api.global.auth.filter;

import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.global.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final String authHeader;
    private final String prefix;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtProvider jwtProvider,
            @Value("${spring.security.auth.header}") String authHeader,
            @Value("${spring.security.auth.prefix}") String prefix,
            UserRepository userRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.authHeader = authHeader;
        this.prefix = prefix;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
            ServletException,
            IOException {

        Optional<String> bearerToken = Optional.ofNullable(request.getHeader(authHeader));
        bearerToken.ifPresent(it -> {
            String accessToken = jwtProvider.extractAccessToken(it);
            jwtProvider.validateAccessToken(accessToken);
            setAuthentication(accessToken);
        });
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        UsernamePasswordAuthenticationToken authentication = jwtProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
