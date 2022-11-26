package com.user.images.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.images.management.entity.User;
import com.user.images.management.repository.UserRepository;
import com.user.images.management.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

	@Override
	public User saveUser(User user) {
		if (user.getUserId() == null) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(user);
	}

	@Override
	public List<User> fetchUsers() {
		List<User> users = new ArrayList<>();  
		userRepository.findAll().forEach(user -> users.add(user));  
		return users;  
	}
	
	@Override 
	public User getUserById(Long id) {  
	return userRepository.findById(id).get();  
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}  

}
