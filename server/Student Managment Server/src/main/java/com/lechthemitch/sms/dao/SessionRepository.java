package com.lechthemitch.sms.dao;

import com.lechthemitch.sms.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
}
