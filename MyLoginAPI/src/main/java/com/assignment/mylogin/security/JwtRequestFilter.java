package com.assignment.mylogin.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.assignment.mylogin.exception.JwtTokenException;
import com.assignment.mylogin.model.ErrorResponse;
import com.assignment.mylogin.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private UserService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException, JwtTokenException {

			final String requestTokenHeader = request.getHeader("Authorization");
	
			String username = null;
			String jwtToken = null;
			// JWT Token is in the form "Bearer token". Remove Bearer word and get
			if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
					jwtToken = requestTokenHeader.substring(7);
					try {
							username = jwtTokenUtil.getUsernameFromToken(jwtToken);
					} catch (IllegalArgumentException | ExpiredJwtException | MalformedJwtException e) {
						
							String errorMessage = null;
							if (e instanceof IllegalArgumentException) {
									errorMessage = "Unable to get JWT Token";
							} else if (e instanceof ExpiredJwtException) {
									errorMessage = "JWT Token has expired";
							} else {
									errorMessage = "Invalid JWT Token";
							}
							ErrorResponse errorResponseMode = new ErrorResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), errorMessage, new Date());
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");
							OutputStream out = response.getOutputStream();
					        ObjectMapper mapper = new ObjectMapper();
					        mapper.writeValue(out, errorResponseMode);
					        out.flush();
					        return;
					}
			} else {
					//throw new JwtTokenException("JWT Token  begin with Bearer String");
			}
	
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
					// authentication
					if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
		
							UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
									userDetails, null, userDetails.getAuthorities());
							usernamePasswordAuthenticationToken
									.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					}
			}
			chain.doFilter(request, response);
	}

}