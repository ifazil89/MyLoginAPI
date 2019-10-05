package com.assignment.mylogin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.assignment.mylogin.model.User;
import com.assignment.mylogin.respository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public User signupUser(User user) {
			System.out.println("signp service called");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			return userRepository.save(user);
	}
	
	public User findByEmailId(String emailId) {
			User user = userRepository.findByEmailId(emailId);
			return user;
	}
	
	public List<User> findAllUser() {
			return userRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailId(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmailId(), user.getPassword(),
				new ArrayList<>());
	}

	@Override
	public void authnticateUser(String userName, String password) {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
	}
}
