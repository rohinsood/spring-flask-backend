package com.nighthawk.spring_portfolio.mvc.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// PURPOSE: Handles authentication failure 
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	// Method is called when unauthenticated user tries to access a secured resource
	@Override
	public void commence(
		HttpServletRequest request, 
		HttpServletResponse response, 
		AuthenticationException authException
		) throws IOException {

		// Sends an HTTP response with status code 401 - redirect client to login page or display error message
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
}