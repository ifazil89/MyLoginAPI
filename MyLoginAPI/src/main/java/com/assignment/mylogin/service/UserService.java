package com.assignment.mylogin.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.assignment.mylogin.model.User;

public interface UserService extends UserDetailsService {

	public User signupUser(User user);
	public User findByEmailId(String email);
	public List<User> findAllUser();
	public void authnticateUser(String userName, String password);
}