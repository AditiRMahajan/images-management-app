package com.user.images.management.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.images.management.repository.UserRepository;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		User user = userRepository.findByEmail(username);
//		if (user == null) {
//			throw new UsernameNotFoundException(String.format("User with %s doesn't exist!", username));
//		}
//		return new UserAdapter(user);
//	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		com.user.images.management.entity.User user = this.userRepository.findByEmail(userName);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User with %s doesn't exist!", userName));
		}
		return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
	}

}
