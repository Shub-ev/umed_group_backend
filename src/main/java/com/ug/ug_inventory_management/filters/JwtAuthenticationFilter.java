/*
 * Filter checks the requests as per security config and verify the
 * JWT token
 * % Study %
 */

package com.ug.ug_inventory_management.filters;

import com.ug.ug_inventory_management.security.JwtService;
import com.ug.ug_inventory_management.services.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    // Logger
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService, JwtService jwtService) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try{
            String authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendError(response, request, "Missing or invalid Authorization header");
                return;
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if(username == null) {
                sendError(response, request, "Invalid token: username missing");
                return;
            }

            if(SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                if(jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendError(response, request, "Invalid token");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch(ExpiredJwtException exception) {
            sendError(response, request, "Token expired");
            return;
        } catch (JwtException exception) {
            sendError(response, request, "Invalid token");
            return;
        }
    }

    private void sendError(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        log.warn("JWT failed for URI: {} | Reason: {}", request.getRequestURI(), message);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
                {
                    "timestamp": "%s",
                    "message": "%s",
                    "status": 401
                }
                """.formatted(java.time.LocalDateTime.now(), message));
    }

    // we add this method to bypass dofilterinternal method as JwtFilter takes place
    // before SecurityConfig requestMatchers
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/employee/login") ||
                path.equals("/hkfu/login");
    }
}
