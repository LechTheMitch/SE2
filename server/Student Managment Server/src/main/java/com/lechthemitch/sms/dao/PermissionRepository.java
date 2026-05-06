package com.lechthemitch.sms.dao;

import com.lechthemitch.sms.entity.Permission;
import com.lechthemitch.sms.entity.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByName(PermissionType permissionType);
}
