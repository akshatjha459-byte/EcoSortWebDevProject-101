package com.ecosort.security;

import com.ecosort.auth.model.User;
import com.ecosort.auth.repository.UserRepository;
import com.ecosort.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(ObjectProvider<JwtService> jwtServiceProvider, ObjectProvider<UserRepository> userRepositoryProvider) {
        this.jwtService = jwtServiceProvider.getIfAvailable();
        this.userRepository = userRepositoryProvider.getIfAvailable();
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (jwtService == null || userRepository == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String userId = jwtService.extractUserId(token);
            String email = jwtService.extractEmail(token);
            if (userId != null && email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findById(userId);
                if (user != null && user.email().equalsIgnoreCase(email)) {
                    UserPrincipal principal = new UserPrincipal(user);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
