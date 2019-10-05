package com.assignment.mylogin.exception;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.assignment.mylogin.model.ErrorResponse;

@Order (Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{

		/**
		 * model object validation exception handling
		 */
		@Override
		protected ResponseEntity<Object> handleMethodArgumentNotValid(
				MethodArgumentNotValidException ex,
                HttpHeaders headers,
                HttpStatus status, WebRequest request) {
				List<String> errorsDetails = ex.getBindingResult()
											.getFieldErrors()
							                .stream()
							                .map(e -> e.getDefaultMessage())
							                .collect(Collectors.toList());
				ErrorResponse responseModel = new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Missing Fields/Not Valid fields", new Date(),errorsDetails);
				return new ResponseEntity<>(responseModel, headers, HttpStatus.BAD_REQUEST);
		}
		
		/**
		 * Invalid credential exception
		 * @param ex
		 * @return
		 */
		@ExceptionHandler (BadCredentialsException.class)		
		public ResponseEntity<Object> handleBadCredentialsException(
				BadCredentialsException ex) {
				List<String> errorsDetails = Arrays.asList(ex.getMessage());
				ErrorResponse responseModel = new ErrorResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid username / Password", new Date(),errorsDetails);
				return new ResponseEntity<>(responseModel, HttpStatus.UNAUTHORIZED);
		}
		
		/**
		 * Duplicate user name found exception
		 * @param ex
		 * @return
		 */
		@ExceptionHandler (DuplicateUserFoundException.class)		
		public ResponseEntity<Object> handleDuplicateUserFoundException(
				DuplicateUserFoundException ex) {
				List<String> errorsDetails = null;
				ErrorResponse responseModel = new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Duplicate user found for the user name", new Date(),errorsDetails);
				return new ResponseEntity<>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		@ExceptionHandler (JwtTokenException.class)		
		public ResponseEntity<Object> handleMalformedJwtException(
				JwtTokenException ex) {
				List<String> errorsDetails = null;
				ErrorResponse responseModel = new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage(), new Date(),errorsDetails);
				return new ResponseEntity<>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
}
