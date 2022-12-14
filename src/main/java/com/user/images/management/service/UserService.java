package com.user.images.management.service;

import java.util.List;

import com.user.images.management.entity.User;

public interface UserService {
	
    // Save User
    User saveUser(User user);
 
    // Read Users
    List<User> fetchUsers();
    
    // Find User by id
    User getUserById(Long id);
    
    // Find User by Email
    User findUserByEmail(String email);

}
