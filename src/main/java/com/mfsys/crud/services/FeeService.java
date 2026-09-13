package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Fee;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.FeeRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.dto.FeeRequest;
import com.mfsys.crud.dto.FeeResponseDto;
import com.mfsys.crud.dto.FeeSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeeService {

    private  final FeeRepo feeRepo;
    private final StudentRepo studentRepo;
    private static final double RATE_PER_CREDIT_HOUR = 5000;
    private static final double FIXED_SEMESTER_CHARGE = 15000;

    public FeeService(FeeRepo feeRepo, StudentRepo studentRepo){
        this.feeRepo = feeRepo;
        this.studentRepo=studentRepo;
    }

    public double calculateSuggestedFee(int creditHours) {
        return (creditHours * RATE_PER_CREDIT_HOUR) + FIXED_SEMESTER_CHARGE;
    }

    public FeeResponseDto createFee(int studentId, FeeRequest request){
        Student student = studentRepo.findById((long) studentId).orElseThrow(()-> new RuntimeException("Student not found"));

        Fee newFee = new Fee();
        newFee.setStudent(student);
        newFee.setSemester(request.getSemester());
        newFee.setTotalFee(request.getTotalFee());
        newFee.setPaidFee(request.getPaidFee());
        newFee.setStatus(calculateStatus(request.getTotalFee(), request.getPaidFee()));

        Fee saved = feeRepo.save(newFee);
        return  convertToDto(saved);
    }

    public List<FeeResponseDto> getAllFees() {
        return feeRepo.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public Set<FeeResponseDto> getFeesByStudentId(int studentId) {
        return feeRepo.findByStudent_Id((long) studentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toSet());
    }


    public List<FeeResponseDto> getMyFee(String email)
    {
        return  feeRepo.findByStudent_User_Email(email).stream().map(this::convertToDto).toList();
    }

    public FeeResponseDto updateFee(int feeId, FeeRequest request) {
        Fee fee = feeRepo.findById((long) feeId).orElseThrow(() -> new RuntimeException("Fee record not found"));

        fee.setSemester(request.getSemester());
        fee.setTotalFee(request.getTotalFee());
        fee.setPaidFee(request.getPaidFee());
        fee.setStatus(calculateStatus(request.getTotalFee(), request.getPaidFee()));

        Fee updated = feeRepo.save(fee);
        return convertToDto(updated);


    }

    public void deleteFee(int feeId){
        feeRepo.deleteById((long) feeId);
    }



// Fee Summary
    public FeeSummaryDto getMyFeeSummary(String email)
    {
        List<FeeResponseDto> fees = feeRepo.findByStudent_User_Email(email).stream().map(this::convertToDto).toList();

        double totalPaid = fees.stream().mapToDouble(FeeResponseDto::getPaidFee).sum();

        double totalDue = fees.stream().mapToDouble(FeeResponseDto::getDueAmount).sum();

        double totalOverall = totalPaid+totalDue;

         int paidPercentage = totalOverall>0 ? (int) Math.round((totalPaid/totalOverall)*100):0;

         return  new FeeSummaryDto(totalPaid,totalDue,totalOverall,paidPercentage);

    }



//    StudentFeeSummary
    public  FeeSummaryDto getStudentFeeSummary(int studentId){
        List<FeeResponseDto> fees = feeRepo.findByStudent_Id((long) studentId)
                .stream()
                .map(this::convertToDto)
                .toList();

        double totalPaid = fees.stream().mapToDouble(FeeResponseDto::getPaidFee).sum();
        double totalDue = fees.stream().mapToDouble(FeeResponseDto::getDueAmount).sum();
        double totalOverall = totalPaid + totalDue;

        int paidPercentage = totalOverall > 0
                ? (int) Math.round((totalPaid / totalOverall) * 100)
                : 0;

        return new FeeSummaryDto(totalPaid, totalDue, totalOverall, paidPercentage);
    }



//    status-calculation method
private String calculateStatus(double totalFee, double paidFee) {
    if (paidFee >= totalFee && totalFee > 0) return "PAID";
    if (paidFee > 0 && paidFee < totalFee) return "PENDING";
    return "OVERDUE";
}



//get available semesters
    public List<String> getAvailableSemesters(int studentId)
    {
        Student student = studentRepo.findById((long) studentId).orElseThrow(()-> new RuntimeException("Student not found"));

        Integer admissionYear = student.getUser().getAdmissionYear();
        if(admissionYear==null){
    return  List.of();
    }

        List<String> allSemesters = new java.util.ArrayList<>();
        for(int i  = 0; i<4; i++){
            int year = admissionYear+i;
            allSemesters.add("Fall " + year);
            allSemesters.add("Summer " + year);
        }
        Set<String> usedSemesters = feeRepo.findByStudent_Id((long) studentId)
                .stream()
                .map(fee -> fee.getSemester().trim().toLowerCase())
                .collect(Collectors.toSet());

        return allSemesters.stream()
                .filter(sem -> !usedSemesters.contains(sem.trim().toLowerCase()))
                .toList();
    }


    private FeeResponseDto convertToDto(Fee fee){
        double due = fee.getTotalFee()-fee.getPaidFee();

        return new FeeResponseDto(
                fee.getId(),
                fee.getStudent().getId(),
                fee.getStudent().getUser().getUsername(),
                fee.getSemester(),
                fee.getTotalFee(),
                fee.getPaidFee(),
                due,fee.getStatus()
        );
    }
}
