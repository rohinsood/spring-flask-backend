package com.nighthawk.spring_portfolio.mvc.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

// PURPOSE: Intercept incoming requests, processes the JWT, & sets up Spring Security's authentication context

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private PersonDetailsService personDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, 
		// Intercepts every request, extract data, and provide new data in response
		HttpServletResponse response, 
		// Contains list of other filters that we need to execute 
		FilterChain chain
		) throws ServletException, IOException {

		// Retrieve cookies from the request  
		final Cookie[] cookies = request.getCookies();
		String username = null;
		String jwtToken = null;

		// Tries to retrieve JWT from the "jwt" cookie
		if ((cookies == null) || (cookies.length == 0)) {
			logger.warn("No cookies");
		} else {
			// Iterate to find
			for (Cookie cookie: cookies) {
				if (cookie.getName().equals("jwt")) {
					jwtToken = cookie.getValue();
				}
			}
			if (jwtToken == null) {
				logger.warn("No jwt cookie");
			} else {
				try {
					// If JWT is present, extract username from the token & validate using JwtTokenUtil
					username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				} catch (IllegalArgumentException e) {
					System.out.println("Unable to get JWT Token");
				} catch (ExpiredJwtException e) {
					System.out.println("JWT Token has expired");
				} catch (Exception e) {
					System.out.println("An error occurred");
				}
			}
		}

		// If token is valid & no existing authentication
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			// Calls loadUserByUsername method from PersonDetailsService file to load user details 
			UserDetails userDetails = this.personDetailsService.loadUserByUsername(username);

			// If user details successfully retrieved & JWT valid, set up the authentication in the Spring Security context
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

				// Create an authentication token 
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}

		// After processing & authentication, request processing continues to next filter
		chain.doFilter(request, response);

		// RESULT: only authenticated users with valid JWTs can access protected resources in the application
	}
}