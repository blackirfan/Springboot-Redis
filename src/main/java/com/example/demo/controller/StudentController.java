package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.repository.ResouceNotFoundException;
import com.example.demo.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @PostMapping("/add-student")
    public Student addEmployee(@RequestBody Student student) {

        return studentRepository.save(student);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        logger.info("Attempting to retrieve student with ID: {}", id);
        Optional<Student> student = studentRepository.findById(id);

        if (student.isPresent()) {
            logger.info("Student with ID {} found", id);
            return ResponseEntity.ok(student.get());
        } else {
            logger.warn("Student with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("redis/{id}")
    @Cacheable(value = "student",key = "#id")
    public Student findStudentById(@PathVariable Long id) {
        System.out.println("Student fetching from database:: "+id);
        logger.info("Attempting to retrieve student with ID: {}", id);
        Optional<Student> student = studentRepository.findById(id);
        logger.info("Student with ID {} found", id);
        return student.get();
    }
    @PutMapping("redis/{studentId}")
    @CachePut(value = "student",key = "#studentId")
    public Student updateStudent(@PathVariable(value = "studentId") Long studentId,
                                   @RequestBody Student student) {
        Student studentOld = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResouceNotFoundException("Student not found for this id :: " + studentId));

        final Student updatedStudent = studentRepository.save(studentOld);
        return updatedStudent;

    }
    @DeleteMapping("redis/{id}")
    @CacheEvict(value = "student", allEntries = true)
    public void deleteStudent(@PathVariable(value = "id") Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new ResouceNotFoundException("Student not found" + studentId));
        studentRepository.delete(student);
    }
}
