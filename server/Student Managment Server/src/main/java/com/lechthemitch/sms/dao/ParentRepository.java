package com.lechthemitch.sms.dao;

import com.lechthemitch.sms.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Integer> {
    Optional<Parent> findByUserId(Integer userId);
}
