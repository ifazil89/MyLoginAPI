package com.assignment.mylogin.respository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.assignment.mylogin.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

		public User findByEmailId(String emailId);
}
