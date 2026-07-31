package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.StudentRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsService {
    private final StudentRepo studentRepo;

    public  StudentsService(StudentRepo studentRepo)
    {
        this.studentRepo=studentRepo;
    }

    public List<Student> getAllStudents()
    {
        return studentRepo.findAll();
    }


    public List<Student> createStudents(List<Student> student) {
        return studentRepo.saveAll(student);
    }

    public  Student getStudentbyId(int id)
    {

        return studentRepo.findById((long) id).orElse(null);
    }

    public Student updateStudent(int id,Student newData)
    {
        Student existStudent= studentRepo.findById((long) id).orElse(null);
        if(existStudent!=null)
        {
            existStudent.setFname(newData.getFname());
            existStudent.setEmail(newData.getEmail());
            return studentRepo.save(existStudent);
        }
        return null;
    }

    public void deleteStudent(int id)
    {
      studentRepo.deleteById((long) id);
    }

    public Student updateStudentEmail(int id,Student updateEmail)
    {
        Student existStudent= studentRepo.findById((long) id).orElse(null);
        if(existStudent!=null)
        {

            existStudent.setEmail(updateEmail.getEmail());
            return studentRepo.save(existStudent);
        }
        return null;
    }
}
