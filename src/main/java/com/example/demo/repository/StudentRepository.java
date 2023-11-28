package com.example.demo.repository;

import com.example.demo.entity.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByFirstName(String firstName);

    // Example: Find students by last name
    List<Student> findByLastName(String lastName);

    // You can also define queries using @Query annotation
    // Example: Find students by full name using a custom query
    @Query("SELECT s FROM Student s WHERE CONCAT(s.firstName, ' ', s.lastName) = :fullName")
    List<Student> findByFullName(@Param("fullName") String fullName);

}
