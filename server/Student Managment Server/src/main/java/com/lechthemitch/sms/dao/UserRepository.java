package com.lechthemitch.sms.dao;

import com.lechthemitch.sms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> getUserInfoByEmail(String mail);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
