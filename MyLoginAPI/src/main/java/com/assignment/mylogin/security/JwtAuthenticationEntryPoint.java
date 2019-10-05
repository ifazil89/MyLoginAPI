package com.assignment.mylogin.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.assignment.mylogin.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		
			ErrorResponse errorResponseMode = new ErrorResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Unauthorized Access / Token starts with Bearer ", new Date());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			OutputStream out = response.getOutputStream();
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.writeValue(out, errorResponseMode);
	        out.flush();
	        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        out.close();
			
	}
}