package com.lechthemitch.sms.dao;

import com.lechthemitch.sms.entity.Permission;
import com.lechthemitch.sms.entity.Role;
import com.lechthemitch.sms.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleType roleType);
}
