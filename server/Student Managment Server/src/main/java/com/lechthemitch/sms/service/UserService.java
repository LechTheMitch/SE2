package com.lechthemitch.sms.service;

import com.lechthemitch.sms.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(int theId);

    User save(User user);

    void deleteById(int theId);

    User findByPhoneNumber(String phoneNumber);
}
