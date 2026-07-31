package com.mfsys.crud.Controller;

import java.beans.ConstructorProperties;
import  java.util.*;
import  java.util.List;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.StudentRepo;
import org.springframework.web.bind.annotation.*;
import com.mfsys.crud.services.StudentsService;
import java.util.List;

@RestController
@RequestMapping("api/students")
public class StudentController {
private final StudentsService studentService;
    private final StudentRepo studentRepo;

//    private final StudentRepo studentRepo;

    public  StudentController(StudentsService studentService, StudentRepo studentRepo)
    {
        this.studentService=studentService;
        this.studentRepo = studentRepo;
    }

    @GetMapping
    public List<Student> getAllStudents()
    {
        return studentService.getAllStudents();
    }

    @PostMapping
    public List<Student> createStudents(@RequestBody List<Student> student)
    {
return studentService.createStudents(student);
    }

    @GetMapping("/{id}")
    public Student getStudentByid(@PathVariable int id)
    {
        return studentService.getStudentbyId(id);
    }

@PutMapping("/{id}")
    public Student updatedStudent(@PathVariable int id, @RequestBody Student newData)
{
    return studentService.updateStudent(id, newData);
}

@DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable int id)
{
    studentService.deleteStudent(id);
}

@PatchMapping("/{id}")
    public Student updateStudentEmail(@PathVariable int id, @RequestBody Student updateEmail)
{
    return studentService.updateStudentEmail(id,updateEmail);
}
}
