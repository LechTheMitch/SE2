package com.lechthemitch.student_management.dao

import com.lechthemitch.student_management.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository: JpaRepository<Student, Int> {
}