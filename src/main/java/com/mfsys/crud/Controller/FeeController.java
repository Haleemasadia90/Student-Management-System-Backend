package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.FeeRequest;
import com.mfsys.crud.dto.FeeResponseDto;
import com.mfsys.crud.services.FeeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee")
public class FeeController {

    private  final FeeService feeService;

    public  FeeController(FeeService feeService)
    {
        this.feeService= feeService;
    }

    @PostMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public FeeResponseDto createFee(@PathVariable int studentId, @Valid @RequestBody FeeRequest request)
    {
        return  feeService.createFee(studentId,request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeeResponseDto> getAllFees() {
        return feeService.getAllFees();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeeResponseDto> getFeesByStudentId(@PathVariable int studentId){
        return feeService.getFeesByStudentId(studentId);
    }

    @GetMapping("/calculate")
    public double calculateFee(@RequestParam int creditHours) {
        return feeService.calculateSuggestedFee(creditHours);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public List<FeeResponseDto> getMyFees(Authentication authentication){
        String email = authentication.getName();
        return  feeService.getMyFee(email);
    }

    @PutMapping("/{feeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public FeeResponseDto updateFee(@PathVariable int feeId, @Valid @RequestBody FeeRequest request){
        return feeService.updateFee(feeId,request);
    }

    @DeleteMapping("/{feeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFee(@PathVariable int feeId){
        feeService.deleteFee(feeId);
    }
}
