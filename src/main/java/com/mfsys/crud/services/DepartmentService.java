package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Department;
import com.mfsys.crud.Repository.DepartmentRepo;
import com.mfsys.crud.dto.DepartmentRequest;
import com.mfsys.crud.dto.DepartmentResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepo departmentRepo;

    public DepartmentService(DepartmentRepo departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepo.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public DepartmentResponseDto createDepartment(DepartmentRequest request) {
        Department newDept = new Department();
        newDept.setName(request.getName());

        Department saved = departmentRepo.save(newDept);
        return convertToDto(saved);
    }



    public DepartmentResponseDto updateDepartment(int id, DepartmentRequest request) {

        Department department = departmentRepo.findById((long) id).orElseThrow(() ->
                        new RuntimeException("Department not found with id: " + id));

        department.setName(request.getName());
        Department updated = departmentRepo.save(department);
        return convertToDto(updated);
    }


    public void deleteDepartment(int id) {
        departmentRepo.deleteById((long) id);
    }


//    calculate total department
public long getTotalDepartmentsCount() {
    return departmentRepo.count();
}


    private DepartmentResponseDto convertToDto(Department department) {
        return new DepartmentResponseDto(
                department.getId(),
                department.getName()
        );
    }
}