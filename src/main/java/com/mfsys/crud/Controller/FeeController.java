package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.FeeRequest;
import com.mfsys.crud.dto.FeeResponseDto;
import com.mfsys.crud.dto.FeeSummaryDto;
import com.mfsys.crud.services.FeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Set<FeeResponseDto> getFeesByStudentId(@PathVariable int studentId) {
        return feeService.getFeesByStudentId(studentId);
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

// fee summary
@GetMapping("/me/summary")
@PreAuthorize("hasRole('STUDENT')")
public FeeSummaryDto getMyFeeSummary(
        Authentication authentication,
        @RequestParam(required = false) String semester
) {
    String email = authentication.getName();

    if (semester == null || semester.isBlank()) {
        return feeService.getMyFeeSummary(email);
    }

    return feeService.getMyFeeSummary(email, semester);
}

    //    StudentFeeSummary
    @GetMapping("/student/{studentId}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public FeeSummaryDto getStudentFeeSummary(@PathVariable int studentId) {
        return feeService.getStudentFeeSummary(studentId);
    }


    //get available semesters
    @GetMapping("/student/{studentId}/available-semesters")
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> getAvailableSemesters(@PathVariable int studentId) {
        return feeService.getAvailableSemesters(studentId);
    }


    @GetMapping("/student/{studentId}/credit-hours")
    @PreAuthorize("hasRole('ADMIN')")
    public int getStudentCreditHours(
            @PathVariable int studentId,
            @RequestParam String semester
    ) {
        return feeService.getStudentCreditHours(
                studentId, semester
        );
    }


    //    calculate overall fee summary
@GetMapping("/summary")
@PreAuthorize("hasRole('ADMIN')")
public FeeSummaryDto getOverallFeeSummary() {
    return feeService.getOverallFeeSummary();
}

    @GetMapping("/me/semester")
    @PreAuthorize("hasRole('STUDENT')")
    public List<FeeResponseDto> getMyFeeBySemester(
            Authentication authentication,
            @RequestParam String semester
    ) {
        return feeService.getMyFeeBySemester(
                authentication.getName(),
                semester
        );
    }


    @GetMapping("/me/semester/paged")
    @PreAuthorize("hasRole('STUDENT')")
    public Page<FeeResponseDto> getMyFeeBySemesterPaged(
            Authentication authentication,
            @RequestParam String semester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return feeService.getMyFeeBySemesterPaged(
                authentication.getName(),
                semester,
                pageable
        );
    }


    @GetMapping("/me/semesters")
    @PreAuthorize("hasRole('STUDENT')")
    public List<String> getMyAvailableSemesters(
            Authentication authentication
    ) {
        return feeService.getMyAvailableSemesters(
                authentication.getName()
        );
    }

    // one-time repair of existing data: POST /api/fee/sync-all
    @PostMapping("/sync-all")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Integer> syncAll() {
        return Map.of("synced", feeService.syncAllFees());
    }

    @GetMapping("/me/paged")
    @PreAuthorize("hasRole('STUDENT')")
    public Page<FeeResponseDto> getMyFeesPaged(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = authentication.getName();

        Pageable pageable = PageRequest.of(page, size);

        return feeService.getMyFeePaged(email, pageable);
    }
}
