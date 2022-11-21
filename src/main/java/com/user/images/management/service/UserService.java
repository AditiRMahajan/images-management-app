package com.user.images.management.service;

import java.util.List;

import com.user.images.management.entity.User;

public interface UserService {
	
    // Save operation
    User saveUser(User user);
 
    // Read operation
    List<User> fetchUsers();
    
    // Find User by id
    User getUserById(Long id);
    
    // Find User by Email
    User findUserByEmail(String email);

}
