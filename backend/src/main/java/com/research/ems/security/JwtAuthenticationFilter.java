package com.research.ems.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            UserDetailsService userDetailsService,
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (authorization == null ||
                !authorization.startsWith(SecurityConstants.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(SecurityConstants.BEARER_PREFIX.length());
        try {
            String username = jwtTokenProvider.extractUsername(token);
            if (username == null || username.isBlank()) {
                throw new BadCredentialsException("Invalid bearer token");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);
                if (!userDetails.isEnabled() ||
                        !jwtTokenProvider.isTokenValid(token, userDetails)) {
                    throw new BadCredentialsException("Invalid bearer token");
                }
                var authentication = new org.springframework.security
                        .authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException ex) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid bearer token", ex));
            return;
        } catch (AuthenticationException ex) {
            authenticationEntryPoint.commence(request, response, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
