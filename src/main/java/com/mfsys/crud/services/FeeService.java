package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Fee;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.FeeRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.dto.FeeRequest;
import com.mfsys.crud.dto.FeeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeService {

    private  final FeeRepo feeRepo;
    private final StudentRepo studentRepo;

    public FeeService(FeeRepo feeRepo, StudentRepo studentRepo){
        this.feeRepo = feeRepo;
        this.studentRepo=studentRepo;
    }

    public FeeResponseDto createFee(int studentId, FeeRequest request){
        Student student = studentRepo.findById((long) studentId).orElseThrow(()-> new RuntimeException("Student not found"));

        Fee newFee = new Fee();
        newFee.setStudent(student);
        newFee.setSemester(request.getSemester());
        newFee.setTotalFee(request.getTotalFee());
        newFee.setPaidFee(request.getPaidFee());
        newFee.setStatus(request.getStatus());

        Fee saved = feeRepo.save(newFee);
        return  convertToDto(saved);
    }

    public List<FeeResponseDto> getFeesByStudentId(int studentId){
        return feeRepo.findByStudent_Id((long) studentId).stream().map(this::convertToDto).toList();
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
        fee.setStatus(request.getStatus());

        Fee updated = feeRepo.save(fee);
        return convertToDto(updated);


    }

    public void deleteFee(int feeId){
        feeRepo.deleteById((long) feeId);
    }

    private FeeResponseDto convertToDto(Fee fee){
        double due = fee.getTotalFee()-fee.getPaidFee();

        return new FeeResponseDto(
                fee.getId(),
                fee.getStudent().getUser().getUsername(),
                fee.getSemester(),
                fee.getTotalFee(),
                fee.getPaidFee(),
                due,fee.getStatus()
        );
    }
}
