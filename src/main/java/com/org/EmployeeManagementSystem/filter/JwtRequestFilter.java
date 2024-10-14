package com.org.EmployeeManagementSystem.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.org.EmployeeManagementSystem.Util.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final List<String> EXCLUDED_PATHS = List.of("/register", "/login");

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtRequestFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        try {
            String path = request.getRequestURI();
            if (EXCLUDED_PATHS.contains(path) || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")) {
                chain.doFilter(request, response);
                return; // Skip JWT processing for these endpoints
            }

            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            if (authorizationHeader == null) {
                handleMissingAuthorizationHeader(response);
                return;
            } else if (authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                try {
                    username = jwtUtil.extractUsername(jwt);
                } catch (Exception e) {
                    handleInvalidToken(response, e);
                    return;
                }
            } else {
                handleInvalidAuthorizationFormat(response);
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, username, jwt);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
            logger.warn("User not found: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not found");
            return;
        }

        chain.doFilter(request, response);
    }

    private void handleMissingAuthorizationHeader(HttpServletResponse response) throws IOException {
        logger.warn("Authorization header is missing");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authorization header is missing");
    }

    private void handleInvalidToken(HttpServletResponse response, Exception e) throws IOException {
        logger.error("Error extracting username from token: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid token");
    }

    private void handleInvalidAuthorizationFormat(HttpServletResponse response) throws IOException {
        logger.warn("Invalid Authorization header format");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid Authorization header format");
    }

    private void authenticateUser(HttpServletRequest request, String username, String jwt) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        if (jwtUtil != null && jwtUtil.validateToken(jwt, userDetails.getUsername())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
