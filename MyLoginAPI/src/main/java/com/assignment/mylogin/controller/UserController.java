package com.assignment.mylogin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.mylogin.exception.DuplicateUserFoundException;
import com.assignment.mylogin.model.User;
import com.assignment.mylogin.model.UserResponse;
import com.assignment.mylogin.security.JwtTokenUtil;
import com.assignment.mylogin.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController 
@RequestMapping (path = "/api")
@Api (value = "My Login Api User Controll Api")
public class UserController {

		@Autowired
		private UserService userService;
		
		@Autowired
		private JwtTokenUtil jwtTokenUtil;
		
		@Autowired
		private ModelMapper modelMapper;
		
		@ApiOperation (value = "New user signup (Registration) Api" , response = UserResponse.class)
		@ApiResponses ( value = {
				@ApiResponse (code = 400, message = "Error : Missing required field values"),
				@ApiResponse (code = 500, message = "Error: Duplicate UserId (Email) already found."),
				@ApiResponse (code = 201, message = "User successfully created.")
		})
		@PostMapping (path = "/signup")
		@ResponseStatus (HttpStatus.CREATED)
		public UserResponse singnup(
				@ApiParam (value = "User detail object", required = true)
				@Valid @RequestBody User user) {
				if (userService.findByEmailId(user.getEmailId()) != null) {
						throw new DuplicateUserFoundException("USER ALREADY AVAILABLE");
				}
				userService.signupUser(user);
				UserResponse userResponseModel = new UserResponse();
				modelMapper.map(user, userResponseModel);
				return userResponseModel;
		}
		
		/**
		 * get the token for the user
		 * @param user
		 * @return
		 * @throws Exception
		 */
		@ApiOperation (value = "Login Api with username(emailid) & password and will get the jwt token" , response = String.class)
		@ApiResponses ( value = {
				@ApiResponse (code = 401, message = "Error : Invalid username / Password"),
				@ApiResponse (code = 200, message = "User authenticate successfully and got token")
		})
		@PostMapping (path = "/login")
		@ResponseStatus (HttpStatus.OK)
		public String login(
				@ApiParam (value = "User detail object. Not all the fields required, only email id and password", required = true)
				@RequestBody User user) throws Exception {
				userService.authnticateUser(user.getEmailId(), user.getPassword());
				UserDetails userDetails = userService.loadUserByUsername(user.getEmailId());
				final String token = jwtTokenUtil.generateToken(userDetails);
				return token;
		}
		
		/**
		 * fetch the user information from the header token
		 * @param httpRequest
		 * @return
		 */
		@ApiOperation (value = "Fetch the user information from the header token" , response = UserResponse.class)
		@ApiResponses ( value = {
				@ApiResponse (code = 401, message = "Error : Invalid JWT token or JWT token missing/not started with (Bearer ) in the header"),
				@ApiResponse (code = 200, message = "User information")
		})
		@ApiImplicitParam (name = "Authorization", value = "Access Token (starts with Bearer<space>)", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer jwt_access_token")
		@PostMapping (path = "/userinfo")
		@ResponseStatus (HttpStatus.OK)
		public UserResponse fetchUserInfo(HttpServletRequest httpRequest) {
				String requestTokenHeader = httpRequest.getHeader("Authorization");
				String token = "";
				token = requestTokenHeader.substring(7);
				String username = jwtTokenUtil.getUsernameFromToken(token);
				User user =  userService.findByEmailId(username);
				UserResponse userDto = new UserResponse();
				modelMapper.map(user, userDto);
				return userDto;
		}

}
