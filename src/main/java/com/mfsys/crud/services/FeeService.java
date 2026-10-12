////package com.mfsys.crud.services;
////
////import com.mfsys.crud.Entity.Fee;
////import com.mfsys.crud.Entity.Student;
////import com.mfsys.crud.Repository.FeeRepo;
////import com.mfsys.crud.Repository.StudentRepo;
////import com.mfsys.crud.dto.FeeRequest;
////import com.mfsys.crud.dto.FeeResponseDto;
////import com.mfsys.crud.dto.FeeSummaryDto;
////import org.springframework.stereotype.Service;
////
////import java.util.List;
////import java.util.Set;
////import java.util.stream.Collectors;
////
////@Service
////public class FeeService {
////
////    private  final FeeRepo feeRepo;
////    private final StudentRepo studentRepo;
////
////
////    public FeeService(FeeRepo feeRepo, StudentRepo studentRepo){
////        this.feeRepo = feeRepo;
////        this.studentRepo=studentRepo;
////    }
////
////    public double calculateFee(int creditHours, double ratePerCreditHour, double fixedSemesterCharge) {
////        return (creditHours * ratePerCreditHour) + fixedSemesterCharge;
////    }
////
////
////    private int getStudentTotalCreditHours(Student student) {
////
////        if (student.getCourses() == null) {
////            return 0;
////        }
////
////        return student.getCourses()
////                .stream()
////                .mapToInt(course -> course.getCreditHours())
////                .sum();
////    }
////
////
////
////    public int getStudentCreditHours(int studentId) {
////        Student student = studentRepo.findById((long) studentId)
////                .orElseThrow(() -> new RuntimeException("Student not found"));
////
////        return getStudentTotalCreditHours(student);
////    }
////
////
////    public FeeResponseDto createFee(
////            int studentId,
////            FeeRequest request
////    ) {
////
////        Student student = studentRepo.findById((long) studentId)
////                .orElseThrow(() -> new RuntimeException("Student not found"));
////
////        int totalCreditHours =
////                getStudentTotalCreditHours(student);
////
////        double calculatedTotalFee =
////                calculateFee(
////                        totalCreditHours,
////                        request.getRatePerCreditHour(),
////                        request.getFixedSemesterCharge()
////                );
////
////        Fee newFee = new Fee();
////
////        newFee.setStudent(student);
////        newFee.setSemester(request.getSemester());
////
////        newFee.setRatePerCreditHour(
////                request.getRatePerCreditHour()
////        );
////
////        newFee.setFixedSemesterCharge(
////                request.getFixedSemesterCharge()
////        );
////
////        newFee.setTotalFee(calculatedTotalFee);
////
////        newFee.setPaidFee(request.getPaidFee());
////
////        newFee.setStatus(
////                calculateStatus(
////                        calculatedTotalFee,
////                        request.getPaidFee()
////                )
////        );
////
////        Fee saved = feeRepo.save(newFee);
////
////        return convertToDto(saved);
////    }
////
////    public List<FeeResponseDto> getAllFees() {
////        return feeRepo.findAll()
////                .stream()
////                .map(this::convertToDto)
////                .toList();
////    }
////
////    public Set<FeeResponseDto> getFeesByStudentId(int studentId) {
////        return feeRepo.findByStudent_Id((long) studentId)
////                .stream()
////                .map(this::convertToDto)
////                .collect(Collectors.toSet());
////    }
////
////
////    public List<FeeResponseDto> getMyFee(String email)
////    {
////        return  feeRepo.findByStudent_User_Email(email).stream().map(this::convertToDto).toList();
////    }
////
////    public FeeResponseDto updateFee(
////            int feeId,
////            FeeRequest request
////    ) {
////
////        Fee fee = feeRepo.findById((long) feeId)
////                .orElseThrow(
////                        () -> new RuntimeException("Fee record not found")
////                );
////
////        Student student = fee.getStudent();
////
////        int totalCreditHours =
////                getStudentTotalCreditHours(student);
////
////        double calculatedTotalFee =
////                calculateFee(
////                        totalCreditHours,
////                        request.getRatePerCreditHour(),
////                        request.getFixedSemesterCharge()
////                );
////
////        fee.setSemester(request.getSemester());
////
////        fee.setRatePerCreditHour(
////                request.getRatePerCreditHour()
////        );
////
////        fee.setFixedSemesterCharge(
////                request.getFixedSemesterCharge()
////        );
////        // Automatically calculated
////        fee.setTotalFee(calculatedTotalFee);
////
////        fee.setPaidFee(request.getPaidFee());
////
////        fee.setStatus(
////                calculateStatus(
////                        calculatedTotalFee,
////                        request.getPaidFee()
////                )
////        );
////
////        Fee updated = feeRepo.save(fee);
////
////        return convertToDto(updated);
////    }
////
////    public void deleteFee(int feeId){
////        feeRepo.deleteById((long) feeId);
////    }
////
////
////
////// Fee Summary
////    public FeeSummaryDto getMyFeeSummary(String email)
////    {
////        List<FeeResponseDto> fees = feeRepo.findByStudent_User_Email(email).stream().map(this::convertToDto).toList();
////
////        double totalPaid = fees.stream().mapToDouble(FeeResponseDto::getPaidFee).sum();
////
////        double totalDue = fees.stream().mapToDouble(FeeResponseDto::getDueAmount).sum();
////
////        double totalOverall = totalPaid+totalDue;
////
////         int paidPercentage = totalOverall>0 ? (int) Math.round((totalPaid/totalOverall)*100):0;
////
////         return  new FeeSummaryDto(totalPaid,totalDue,totalOverall,paidPercentage);
////
////    }
////
////
////
//////    StudentFeeSummary
////    public  FeeSummaryDto getStudentFeeSummary(int studentId){
////        List<FeeResponseDto> fees = feeRepo.findByStudent_Id((long) studentId)
////                .stream()
////                .map(this::convertToDto)
////                .toList();
////
////        double totalPaid = fees.stream().mapToDouble(FeeResponseDto::getPaidFee).sum();
////        double totalDue = fees.stream().mapToDouble(FeeResponseDto::getDueAmount).sum();
////        double totalOverall = totalPaid + totalDue;
////
////        int paidPercentage = totalOverall > 0
////                ? (int) Math.round((totalPaid / totalOverall) * 100)
////                : 0;
////
////        return new FeeSummaryDto(totalPaid, totalDue, totalOverall, paidPercentage);
////    }
////
////
////
//////    calculate overall fee summary
////public FeeSummaryDto getOverallFeeSummary() {
////    List<FeeResponseDto> fees = feeRepo.findAll()
////            .stream()
////            .map(this::convertToDto)
////            .toList();
////
////    double totalPaid = fees.stream().mapToDouble(FeeResponseDto::getPaidFee).sum();
////    double totalDue = fees.stream().mapToDouble(FeeResponseDto::getDueAmount).sum();
////    double totalOverall = totalPaid + totalDue;
////
////    int paidPercentage = totalOverall > 0
////            ? (int) Math.round((totalPaid / totalOverall) * 100)
////            : 0;
////
////    return new FeeSummaryDto(totalPaid, totalDue, totalOverall, paidPercentage);
////}
////
////
//////    status-calculation method
////private String calculateStatus(double totalFee, double paidFee) {
////    if (paidFee >= totalFee && totalFee > 0) return "PAID";
////    if (paidFee > 0 && paidFee < totalFee) return "PENDING";
////    return "OVERDUE";
////}
////
////
////
//////get available semesters
////    public List<String> getAvailableSemesters(int studentId)
////    {
////        Student student = studentRepo.findById((long) studentId).orElseThrow(()-> new RuntimeException("Student not found"));
////
////        Integer admissionYear = student.getUser().getAdmissionYear();
////        if(admissionYear==null){
////    return  List.of();
////    }
////
////        List<String> allSemesters = new java.util.ArrayList<>();
////        for(int i  = 0; i<4; i++){
////            int year = admissionYear+i;
////            allSemesters.add("Fall " + year);
////            allSemesters.add("Summer " + year);
////        }
////        Set<String> usedSemesters = feeRepo.findByStudent_Id((long) studentId)
////                .stream()
////                .map(fee -> fee.getSemester().trim().toLowerCase())
////                .collect(Collectors.toSet());
////
////        return allSemesters.stream()
////                .filter(sem -> !usedSemesters.contains(sem.trim().toLowerCase()))
////                .toList();
////    }
////
////
////    private FeeResponseDto convertToDto(Fee fee){
////        double due = fee.getTotalFee()-fee.getPaidFee();
////
////        return new FeeResponseDto(
////                fee.getId(),
////                fee.getStudent().getId(),
////                fee.getStudent().getUser().getUsername(),
////                fee.getSemester(),
////                fee.getRatePerCreditHour(),
////                fee.getFixedSemesterCharge(),
////                fee.getTotalFee(),
////                fee.getPaidFee(),
////                due,fee.getStatus()
////        );
////
////    }
////}
//
//
//package com.mfsys.crud.services;
//
//import com.mfsys.crud.Entity.Enrollment;
//import com.mfsys.crud.Entity.Fee;
//import com.mfsys.crud.Entity.Student;
//import com.mfsys.crud.Repository.EnrollmentRepo;
//import com.mfsys.crud.Repository.FeeRepo;
//import com.mfsys.crud.Repository.StudentRepo;
//import com.mfsys.crud.dto.FeeRequest;
//import com.mfsys.crud.dto.FeeResponseDto;
//import com.mfsys.crud.dto.FeeSummaryDto;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.TreeSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class FeeService {
//
//    private final FeeRepo feeRepo;
//    private final StudentRepo studentRepo;
//    private final EnrollmentRepo enrollmentRepo;
//
//    public FeeService(
//            FeeRepo feeRepo,
//            StudentRepo studentRepo,
//            EnrollmentRepo enrollmentRepo
//    ) {
//        this.feeRepo = feeRepo;
//        this.studentRepo = studentRepo;
//        this.enrollmentRepo = enrollmentRepo;
//    }
//
//    public double calculateFee(
//            int creditHours,
//            double ratePerCreditHour,
//            double fixedSemesterCharge
//    ) {
//        return (creditHours * ratePerCreditHour)
//                + fixedSemesterCharge;
//    }
//
//    public int getStudentCreditHours(
//            int studentId,
//            String semester
//    ) {
//        Student student = studentRepo.findById((long) studentId)
//                .orElseThrow(() ->
//                        new RuntimeException("Student not found")
//                );
//
//        validateSemester(semester);
//
//        return enrollmentRepo
//                .findByStudent_IdAndSemester(
//                        student.getId(), semester.trim()
//                )
//                .stream()
//                .mapToInt(e -> e.getCourse().getCreditHours())
//                .sum();
//    }
//
//    @Transactional
//    public FeeResponseDto createFee(
//            int studentId,
//            FeeRequest request
//    ) {
//        Student student = studentRepo.findById((long) studentId)
//                .orElseThrow(() ->
//                        new RuntimeException("Student not found")
//                );
//
//        String semester = validateSemester(
//                request.getSemester()
//        );
//
//        if (feeRepo.existsByStudent_IdAndSemester(
//                student.getId(), semester)) {
//            throw new IllegalArgumentException(
//                    "A fee record already exists for " + semester
//            );
//        }
//
//        int creditHours = getStudentCreditHours(
//                studentId, semester
//        );
//
//        if (creditHours == 0) {
//            throw new IllegalArgumentException(
//                    "Cannot create a fee without enrolled courses for "
//                            + semester
//            );
//        }
//
//        validateAmounts(request.getRatePerCreditHour(),
//                request.getFixedSemesterCharge(),
//                request.getPaidFee());
//
//        double totalFee = calculateFee(
//                creditHours,
//                request.getRatePerCreditHour(),
//                request.getFixedSemesterCharge()
//        );
//
//        if (request.getPaidFee() > totalFee) {
//            throw new IllegalArgumentException(
//                    "Paid fee cannot exceed total fee"
//            );
//        }
//
//        Fee fee = new Fee();
//        fee.setStudent(student);
//        fee.setSemester(semester);
//        fee.setRatePerCreditHour(
//                request.getRatePerCreditHour()
//        );
//        fee.setFixedSemesterCharge(
//                request.getFixedSemesterCharge()
//        );
//        fee.setTotalFee(totalFee);
//        fee.setPaidFee(request.getPaidFee());
//        fee.setStatus(calculateStatus(
//                totalFee, request.getPaidFee()
//        ));
//
//        return convertToDto(feeRepo.save(fee));
//    }
//
//    public List<FeeResponseDto> getAllFees() {
//        return feeRepo.findAll().stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//    public Set<FeeResponseDto> getFeesByStudentId(
//            int studentId
//    ) {
//        return feeRepo.findByStudent_Id((long) studentId)
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toSet());
//    }
//
//    public List<FeeResponseDto> getMyFee(String email) {
//        return feeRepo.findByStudent_User_Email(email)
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//    public List<FeeResponseDto> getMyFeeBySemester(
//            String email,
//            String semester
//    ) {
//        String validatedSemester = validateSemester(semester);
//
//        return feeRepo
//                .findByStudent_User_EmailAndSemesterIgnoreCase(
//                        email,
//                        validatedSemester
//                )
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//    @Transactional
//    public FeeResponseDto updateFee(
//            int feeId,
//            FeeRequest request
//    ) {
//        Fee fee = feeRepo.findById((long) feeId)
//                .orElseThrow(() ->
//                        new RuntimeException("Fee record not found")
//                );
//
//        String semester = validateSemester(
//                request.getSemester()
//        );
//
//        boolean duplicate = feeRepo
//                .findByStudent_Id(fee.getStudent().getId())
//                .stream()
//                .anyMatch(other ->
//                        !other.getId().equals(fee.getId())
//                                && other.getSemester().equalsIgnoreCase(semester)
//                );
//
//        if (duplicate) {
//            throw new IllegalArgumentException(
//                    "A fee record already exists for " + semester
//            );
//        }
//
//        validateAmounts(request.getRatePerCreditHour(),
//                request.getFixedSemesterCharge(),
//                request.getPaidFee());
//
//        int creditHours = getStudentCreditHours(
//                fee.getStudent().getId().intValue(),
//                semester
//        );
//
//        if (creditHours == 0) {
//            throw new IllegalArgumentException(
//                    "No enrolled courses exist for " + semester
//            );
//        }
//
//        double totalFee = calculateFee(
//                creditHours,
//                request.getRatePerCreditHour(),
//                request.getFixedSemesterCharge()
//        );
//
//        if (request.getPaidFee() > totalFee) {
//            throw new IllegalArgumentException(
//                    "Paid fee cannot exceed total fee"
//            );
//        }
//
//        fee.setSemester(semester);
//        fee.setRatePerCreditHour(
//                request.getRatePerCreditHour()
//        );
//        fee.setFixedSemesterCharge(
//                request.getFixedSemesterCharge()
//        );
//        fee.setTotalFee(totalFee);
//        fee.setPaidFee(request.getPaidFee());
//        fee.setStatus(calculateStatus(
//                totalFee, request.getPaidFee()
//        ));
//
//        return convertToDto(feeRepo.save(fee));
//    }
//
//    public void deleteFee(int feeId) {
//        if (!feeRepo.existsById((long) feeId)) {
//            throw new RuntimeException("Fee record not found");
//        }
//
//        feeRepo.deleteById((long) feeId);
//    }
//
//    public FeeSummaryDto getMyFeeSummary(String email) {
//        return summarize(feeRepo.findByStudent_User_Email(email)
//                .stream().map(this::convertToDto).toList());
//    }
//
//    public FeeSummaryDto getMyFeeSummary(
//            String email,
//            String semester
//    ) {
//        return summarize(getMyFeeBySemester(email, semester));
//    }
//
//    public FeeSummaryDto getStudentFeeSummary(int studentId) {
//        return summarize(feeRepo.findByStudent_Id((long) studentId)
//                .stream().map(this::convertToDto).toList());
//    }
//
//    public FeeSummaryDto getOverallFeeSummary() {
//        return summarize(feeRepo.findAll()
//                .stream().map(this::convertToDto).toList());
//    }
//
//    private FeeSummaryDto summarize(List<FeeResponseDto> fees) {
//        double totalPaid = fees.stream()
//                .mapToDouble(FeeResponseDto::getPaidFee).sum();
//
//        double totalDue = fees.stream()
//                .mapToDouble(FeeResponseDto::getDueAmount).sum();
//
//        double totalOverall = totalPaid + totalDue;
//
//        int paidPercentage = totalOverall > 0
//                ? (int) Math.round(totalPaid / totalOverall * 100)
//                : 0;
//
//        return new FeeSummaryDto(
//                totalPaid, totalDue, totalOverall, paidPercentage
//        );
//    }
//
//    public List<String> getAvailableSemesters(int studentId) {
//        Student student = studentRepo.findById((long) studentId)
//                .orElseThrow(() ->
//                        new RuntimeException("Student not found")
//                );
//
//        Set<String> feeSemesters = feeRepo
//                .findByStudent_Id(student.getId())
//                .stream()
//                .map(f -> normalize(f.getSemester()))
//                .collect(Collectors.toSet());
//
//        return enrollmentRepo.findByStudent_Id(student.getId())
//                .stream()
//                .map(Enrollment::getSemester)
//                .filter(s -> s != null && !s.isBlank())
//                .map(String::trim)
//                .distinct()
//                .filter(s -> !feeSemesters.contains(normalize(s)))
//                .sorted()
//                .toList();
//    }
//
//    private void validateAmounts(
//            double rate,
//            double fixedCharge,
//            double paidFee
//    ) {
//        if (!Double.isFinite(rate) || rate < 0
//                || !Double.isFinite(fixedCharge)
//                || fixedCharge < 0
//                || !Double.isFinite(paidFee)
//                || paidFee < 0) {
//            throw new IllegalArgumentException(
//                    "Fee amounts must be valid non-negative numbers"
//            );
//        }
//    }
//
//    private String validateSemester(String semester) {
//        if (semester == null || semester.isBlank()) {
//            throw new IllegalArgumentException(
//                    "Semester is required"
//            );
//        }
//
//        return semester.trim();
//    }
//
//    private String normalize(String semester) {
//        return semester == null
//                ? ""
//                : semester.trim().toLowerCase();
//    }
//
//    private String calculateStatus(
//            double totalFee,
//            double paidFee
//    ) {
//        if (paidFee >= totalFee && totalFee > 0) {
//            return "PAID";
//        }
//
//        if (paidFee > 0 && paidFee < totalFee) {
//            return "PENDING";
//        }
//
//        return "OVERDUE";
//    }
//
//
//
//    public List<String> getMyAvailableSemesters(String email) {
//        Student student = studentRepo.findByUser_Email(email)
//                .orElseThrow(() ->
//                        new RuntimeException("Student record not found")
//                );
//
//        TreeSet<String> semesters =
//                new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
//
//        enrollmentRepo.findByStudent_Id(student.getId())
//                .stream()
//                .map(Enrollment::getSemester)
//                .filter(s -> s != null && !s.isBlank())
//                .map(String::trim)
//                .forEach(semesters::add);
//
//        feeRepo.findByStudent_Id(student.getId())
//                .stream()
//                .map(Fee::getSemester)
//                .filter(s -> s != null && !s.isBlank())
//                .map(String::trim)
//                .forEach(semesters::add);
//
//        return List.copyOf(semesters);
//    }
//
//
//
//
//    private FeeResponseDto convertToDto(Fee fee) {
//        double due = fee.getTotalFee() - fee.getPaidFee();
//
//        return new FeeResponseDto(
//                fee.getId(),
//                fee.getStudent().getId(),
//                fee.getStudent().getUser().getUsername(),
//                fee.getSemester(),
//                fee.getRatePerCreditHour(),
//                fee.getFixedSemesterCharge(),
//                fee.getTotalFee(),
//                fee.getPaidFee(),
//                due,
//                fee.getStatus()
//        );
//    }
//}











package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Enrollment;
import com.mfsys.crud.Entity.Fee;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.EnrollmentRepo;
import com.mfsys.crud.Repository.FeeRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.dto.FeeRequest;
import com.mfsys.crud.dto.FeeResponseDto;
import com.mfsys.crud.dto.FeeSummaryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class FeeService {

    private final FeeRepo feeRepo;
    private final StudentRepo studentRepo;
    private final EnrollmentRepo enrollmentRepo;

    @Value("${fee.default-rate-per-credit-hour:5000}")
    private double defaultRate;

    @Value("${fee.default-fixed-semester-charge:15000}")
    private double defaultFixedCharge;

    public FeeService(
            FeeRepo feeRepo,
            StudentRepo studentRepo,
            EnrollmentRepo enrollmentRepo
    ) {
        this.feeRepo = feeRepo;
        this.studentRepo = studentRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    public double calculateFee(int creditHours, double ratePerCreditHour, double fixedSemesterCharge) {
        return (creditHours * ratePerCreditHour) + fixedSemesterCharge;
    }

    public int getStudentCreditHours(int studentId, String semester) {
        Student student = studentRepo.findById((long) studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        validateSemester(semester);

        return enrollmentRepo
                .findByStudent_IdAndSemester(student.getId(), semester.trim())
                .stream()
                .mapToInt(e -> e.getCourse().getCreditHours())
                .sum();
    }

    // =====================================================
    // AUTO SYNC: keeps the fee in line with enrollments
    // =====================================================

    /** Call after any enrollment is added or removed. */
    @Transactional
    public void syncFeeForSemester(Long studentId, String semester) {
        String sem = validateSemester(semester);

        int creditHours = enrollmentRepo
                .findByStudent_IdAndSemester(studentId, sem)
                .stream()
                .mapToInt(e -> e.getCourse().getCreditHours())
                .sum();

        Fee fee = feeRepo
                .findByStudent_IdAndSemesterIgnoreCase(studentId, sem)
                .orElse(null);

        if (fee == null) {
            if (creditHours == 0) {
                return;
            }

            Student student = studentRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            fee = new Fee();
            fee.setStudent(student);
            fee.setSemester(sem);
            fee.setRatePerCreditHour(defaultRate);
            fee.setFixedSemesterCharge(defaultFixedCharge);
            fee.setPaidFee(0.0);
        } else if (creditHours == 0 && fee.getPaidFee() == 0) {
            // all courses dropped and nothing paid -> remove the fee
            feeRepo.delete(fee);
            return;
        }

        double total = calculateFee(
                creditHours,
                fee.getRatePerCreditHour(),
                fee.getFixedSemesterCharge()
        );

        fee.setTotalFee(total);
        fee.setStatus(calculateStatus(total, fee.getPaidFee()));
        feeRepo.save(fee);
    }

    /** One-time repair for enrollments that already exist. */
    @Transactional
    public int syncAllFees() {
        Set<String> done = new HashSet<>();
        int count = 0;

        for (Enrollment e : enrollmentRepo.findAll()) {
            if (e.getSemester() == null || e.getSemester().isBlank()) {
                continue;
            }

            Long studentId = e.getStudent().getId();
            String sem = e.getSemester().trim();

            if (done.add(studentId + "|" + sem.toLowerCase())) {
                syncFeeForSemester(studentId, sem);
                count++;
            }
        }

        return count;
    }

    @Transactional
    public FeeResponseDto createFee(int studentId, FeeRequest request) {
        Student student = studentRepo.findById((long) studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String semester = validateSemester(request.getSemester());

        if (feeRepo.existsByStudent_IdAndSemester(student.getId(), semester)) {
            throw new IllegalArgumentException(
                    "A fee record already exists for " + semester);
        }

        int creditHours = getStudentCreditHours(studentId, semester);

        if (creditHours == 0) {
            throw new IllegalArgumentException(
                    "Cannot create a fee without enrolled courses for " + semester);
        }

        validateAmounts(request.getRatePerCreditHour(),
                request.getFixedSemesterCharge(),
                request.getPaidFee());

        double totalFee = calculateFee(
                creditHours,
                request.getRatePerCreditHour(),
                request.getFixedSemesterCharge()
        );

        if (request.getPaidFee() > totalFee) {
            throw new IllegalArgumentException("Paid fee cannot exceed total fee");
        }

        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setSemester(semester);
        fee.setRatePerCreditHour(request.getRatePerCreditHour());
        fee.setFixedSemesterCharge(request.getFixedSemesterCharge());
        fee.setTotalFee(totalFee);
        fee.setPaidFee(request.getPaidFee());
        fee.setStatus(calculateStatus(totalFee, request.getPaidFee()));

        return convertToDto(feeRepo.save(fee));
    }

    public List<FeeResponseDto> getAllFees() {
        return feeRepo.findAll().stream().map(this::convertToDto).toList();
    }

    public Set<FeeResponseDto> getFeesByStudentId(int studentId) {
        return feeRepo.findByStudent_Id((long) studentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toSet());
    }

    public List<FeeResponseDto> getMyFee(String email) {
        return feeRepo.findByStudent_User_Email(email)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<FeeResponseDto> getMyFeeBySemester(String email, String semester) {
        String validatedSemester = validateSemester(semester);

        return feeRepo
                .findByStudent_User_EmailAndSemesterIgnoreCase(email, validatedSemester)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public FeeResponseDto updateFee(int feeId, FeeRequest request) {
        Fee fee = feeRepo.findById((long) feeId)
                .orElseThrow(() -> new RuntimeException("Fee record not found"));

        String semester = validateSemester(request.getSemester());

        boolean duplicate = feeRepo
                .findByStudent_Id(fee.getStudent().getId())
                .stream()
                .anyMatch(other ->
                        !other.getId().equals(fee.getId())
                                && other.getSemester().equalsIgnoreCase(semester));

        if (duplicate) {
            throw new IllegalArgumentException(
                    "A fee record already exists for " + semester);
        }

        validateAmounts(request.getRatePerCreditHour(),
                request.getFixedSemesterCharge(),
                request.getPaidFee());

        int creditHours = getStudentCreditHours(
                fee.getStudent().getId().intValue(), semester);

        if (creditHours == 0) {
            throw new IllegalArgumentException(
                    "No enrolled courses exist for " + semester);
        }

        double totalFee = calculateFee(
                creditHours,
                request.getRatePerCreditHour(),
                request.getFixedSemesterCharge()
        );

        if (request.getPaidFee() > totalFee) {
            throw new IllegalArgumentException("Paid fee cannot exceed total fee");
        }

        fee.setSemester(semester);
        fee.setRatePerCreditHour(request.getRatePerCreditHour());
        fee.setFixedSemesterCharge(request.getFixedSemesterCharge());
        fee.setTotalFee(totalFee);
        fee.setPaidFee(request.getPaidFee());
        fee.setStatus(calculateStatus(totalFee, request.getPaidFee()));

        return convertToDto(feeRepo.save(fee));
    }

    public void deleteFee(int feeId) {
        if (!feeRepo.existsById((long) feeId)) {
            throw new RuntimeException("Fee record not found");
        }

        feeRepo.deleteById((long) feeId);
    }

    public FeeSummaryDto getMyFeeSummary(String email) {
        return summarize(feeRepo.findByStudent_User_Email(email)
                .stream().map(this::convertToDto).toList());
    }

    public FeeSummaryDto getMyFeeSummary(String email, String semester) {
        return summarize(getMyFeeBySemester(email, semester));
    }

    public FeeSummaryDto getStudentFeeSummary(int studentId) {
        return summarize(feeRepo.findByStudent_Id((long) studentId)
                .stream().map(this::convertToDto).toList());
    }

    public FeeSummaryDto getOverallFeeSummary() {
        return summarize(feeRepo.findAll()
                .stream().map(this::convertToDto).toList());
    }

    private FeeSummaryDto summarize(List<FeeResponseDto> fees) {
        double totalPaid = fees.stream().mapToDouble(FeeResponseDto::getPaidFee).sum();
        double totalDue = fees.stream().mapToDouble(FeeResponseDto::getDueAmount).sum();
        double totalOverall = totalPaid + totalDue;

        int paidPercentage = totalOverall > 0
                ? (int) Math.round(totalPaid / totalOverall * 100)
                : 0;

        return new FeeSummaryDto(totalPaid, totalDue, totalOverall, paidPercentage);
    }

    public List<String> getAvailableSemesters(int studentId) {
        Student student = studentRepo.findById((long) studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Set<String> feeSemesters = feeRepo
                .findByStudent_Id(student.getId())
                .stream()
                .map(f -> normalize(f.getSemester()))
                .collect(Collectors.toSet());

        return enrollmentRepo.findByStudent_Id(student.getId())
                .stream()
                .map(Enrollment::getSemester)
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .filter(s -> !feeSemesters.contains(normalize(s)))
                .sorted()
                .toList();
    }

    public List<String> getMyAvailableSemesters(String email) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student record not found"));

        TreeSet<String> semesters = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        enrollmentRepo.findByStudent_Id(student.getId())
                .stream()
                .map(Enrollment::getSemester)
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .forEach(semesters::add);

        feeRepo.findByStudent_Id(student.getId())
                .stream()
                .map(Fee::getSemester)
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .forEach(semesters::add);

        return List.copyOf(semesters);
    }

    private void validateAmounts(double rate, double fixedCharge, double paidFee) {
        if (!Double.isFinite(rate) || rate < 0
                || !Double.isFinite(fixedCharge) || fixedCharge < 0
                || !Double.isFinite(paidFee) || paidFee < 0) {
            throw new IllegalArgumentException(
                    "Fee amounts must be valid non-negative numbers");
        }
    }

    private String validateSemester(String semester) {
        if (semester == null || semester.isBlank()) {
            throw new IllegalArgumentException("Semester is required");
        }
        return semester.trim();
    }

    private String normalize(String semester) {
        return semester == null ? "" : semester.trim().toLowerCase();
    }

    private String calculateStatus(double totalFee, double paidFee) {
        if (paidFee >= totalFee && totalFee > 0) {
            return "PAID";
        }
        if (paidFee > 0 && paidFee < totalFee) {
            return "PENDING";
        }
        return "OVERDUE";
    }

    private FeeResponseDto convertToDto(Fee fee) {
        double due = fee.getTotalFee() - fee.getPaidFee();

        return new FeeResponseDto(
                fee.getId(),
                fee.getStudent().getId(),
                fee.getStudent().getUser().getUsername(),
                fee.getSemester(),
                fee.getRatePerCreditHour(),
                fee.getFixedSemesterCharge(),
                fee.getTotalFee(),
                fee.getPaidFee(),
                due,
                fee.getStatus()
        );
    }
}