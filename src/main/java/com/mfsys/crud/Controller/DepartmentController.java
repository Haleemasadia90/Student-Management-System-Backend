package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.DepartmentRequest;
import com.mfsys.crud.dto.DepartmentResponseDto;
import com.mfsys.crud.services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService){
        this.departmentService=departmentService;
    }

    @GetMapping
    public List<DepartmentResponseDto> getAllDepartments(){
        return  departmentService.getAllDepartments();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponseDto createDepartment(@Valid @RequestBody DepartmentRequest request){
        return departmentService.createDepartment(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDepartment(@PathVariable int id) {
        departmentService.deleteDepartment(id);
    }

}
