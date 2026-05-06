package com.lechthemitch.sms.dao;

import com.lechthemitch.sms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
	List<Student> findByParentId(Integer parentId);

	java.util.Optional<Student> findByUserId(Integer userId);

	java.util.Optional<Student> findByQrCode(String qrCode);

	boolean existsByQrCode(String qrCode);
}
