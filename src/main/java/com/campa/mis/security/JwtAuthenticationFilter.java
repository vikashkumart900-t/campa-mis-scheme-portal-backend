package com.campa.mis.security;

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.campa.mis.repo.UserAccountRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final UserAccountRepository userAccountRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
			UserAccountRepository userAccountRepository) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.userAccountRepository = userAccountRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(7);

		try {

			String email = jwtService.extractUsername(jwt);

			if (email == null) {
				unauthorized(response, "INVALID_TOKEN", "Invalid authentication token.");
				return;
			}

			if (SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(email);

				if (!jwtService.isTokenValid(jwt, userDetails)) {
					unauthorized(response, "INVALID_TOKEN", "Your authentication token is invalid or expired.");
					return;
				}

				// Compare JWT jti with the latest database token ID.
				String tokenId = jwtService.extractTokenId(jwt);

				Optional<String> activeToken = userAccountRepository.findActiveTokenIdByEmail(email);

				if (activeToken.isEmpty() || activeToken.get() == null || !activeToken.get().equals(tokenId)) {

					unauthorized(response, "SESSION_REPLACED",
							"Your account was logged in from another browser or device.");
					return;
				}

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);

		} catch (io.jsonwebtoken.ExpiredJwtException ex) {

			unauthorized(response, "TOKEN_EXPIRED", "Your session has expired. Please log in again.");

		} catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {

			unauthorized(response, "INVALID_TOKEN", "Invalid authentication token.");

		} catch (Exception ex) {

			SecurityContextHolder.clearContext();
			unauthorized(response, "AUTHENTICATION_FAILED", "Authentication failed.");
		}
	}

	private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {

		SecurityContextHolder.clearContext();

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write("""
				{
				  "success": false,
				  "code": "%s",
				  "message": "%s"
				}
				""".formatted(code, message));
	}
}