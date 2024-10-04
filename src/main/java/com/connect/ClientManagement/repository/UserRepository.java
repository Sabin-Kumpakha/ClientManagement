package com.connect.ClientManagement.repository;

import com.connect.ClientManagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByEmail(String email);

    public User findByPhoneNumber(String phoneNumber);

    public User findByEmailAndPhoneNumber(String email, String phoneNumber);
}
