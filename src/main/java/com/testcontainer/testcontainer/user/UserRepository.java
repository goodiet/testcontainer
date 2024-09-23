package com.testcontainer.testcontainer.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // No need to add methods yet, JpaRepository provides basic CRUD methods
}