package com.user.images.management.repository;

import org.springframework.stereotype.Repository;

import com.user.images.management.entity.User;

import org.springframework.data.repository.CrudRepository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmail(String email);

}
