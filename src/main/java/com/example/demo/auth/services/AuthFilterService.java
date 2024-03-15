package com.example.demo.auth.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-11-2024
 **/

@Service
@Slf4j
public class AuthFilterService extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    public AuthFilterService(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String apiName = null;
        apiName = (request.getRequestURI());

        if(apiName.contains("validate")){
            filterChain.doFilter(request, response);
            return;
        }
        if (shouldAllowUnauthenticatedAccess(apiName, authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        // extract jwt
        assert authHeader != null;
        String jwt = authHeader.substring(7);

        String username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails) && !apiName.contains("validate")) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    private boolean shouldAllowUnauthenticatedAccess(String apiName, String authHeader) {
        if (apiName != null && (authHeader == null || !authHeader.startsWith("Bearer "))) {
            String[] allowedApis = {"register", "login", "fake", "create"};
            for (String allowedApi : allowedApis) {
                if (apiName.contains(allowedApi)) {
                    return true;
                }
            }
        }
        return false;
    }
}
