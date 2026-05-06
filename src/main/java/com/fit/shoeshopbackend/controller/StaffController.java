package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.model.Staff;
import com.fit.shoeshopbackend.model.WorkStatus;
import com.fit.shoeshopbackend.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<Page<Staff>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Staff> result = staffService.search(search, department, status, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> get(@PathVariable String id) {
        return staffService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Staff> create(@RequestBody Staff staff) {
        validateStaff(staff, true);
        if (staffService.findById(staff.getStaffId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff ID already exists");
        }
        // Set default status: Active
        if (staff.getWorkStatus() == null) {
            staff.setWorkStatus(WorkStatus.Active);
        }
        Staff saved = staffService.save(staff);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> update(@PathVariable String id, @RequestBody Staff staff) {
        validateStaff(staff, false);
        if (staff.getStaffId() != null && !staff.getStaffId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff ID does not match");
        }
        return staffService.update(id, staff)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        staffService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public void exportCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=staffs.csv");

        List<Staff> list = staffService.searchAll(search, department, status);

        // write header
        String header = "staffId,fullName,phoneNumber,citizenId,birthDate,workStatus,gender,position,department\n";
        response.getWriter().write(header);

        for (Staff s : list) {
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    s.getStaffId(),
                    s.getFullName() == null ? "" : s.getFullName().replaceAll(",", " "),
                    s.getPhoneNumber() == null ? "" : s.getPhoneNumber(),
                    s.getCitizenId() == null ? "" : s.getCitizenId(),
                    s.getBirthDate() == null ? "" : s.getBirthDate().toString(),
                    s.getWorkStatus() == null ? "" : s.getWorkStatus().name(),
                    s.getGender() == null ? "" : s.getGender().name(),
                    s.getPosition() == null ? "" : s.getPosition().name(),
                    s.getDepartment() == null ? "" : s.getDepartment().name()
            );
            response.getWriter().write(line);
        }

        response.getWriter().flush();
    }

    private void validateStaff(Staff s, boolean isCreate) {
        if (s == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff data is empty");

        // Staff ID validation
        if (s.getStaffId() == null || !s.getStaffId().matches("^NV\\d{3}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff ID must be in format NVXXX (e.g., NV001)");
        }

        // Full name validation
        if (s.getFullName() == null || s.getFullName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name cannot be empty");
        }

        // Email validation
        if (s.getEmail() == null || s.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
        }
        if (!s.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }

        // Phone number validation
        if (s.getPhoneNumber() != null && !s.getPhoneNumber().isEmpty()) {
            if (!s.getPhoneNumber().matches("^0\\d{9}$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number must be 10 digits starting with 0");
            }
        }

        // Citizen ID validation
        if (s.getCitizenId() != null && !s.getCitizenId().isEmpty()) {
            if (!s.getCitizenId().matches("^0\\d{11}$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Citizen ID must be 12 digits starting with 0");
            }
        }

        // Birth date validation
        if (s.getBirthDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate birthDate = s.getBirthDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (birthDate.isAfter(today)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birth date cannot be in the future");
            }

            int age = today.getYear() - birthDate.getYear();
            if (today.getMonthValue() < birthDate.getMonthValue() ||
                    (today.getMonthValue() == birthDate.getMonthValue() && today.getDayOfMonth() < birthDate.getDayOfMonth())) {
                age--;
            }

            if (age < 15) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age must be 15 or older");
            }
        }

        // Position validation
        if (s.getPosition() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Position cannot be empty");
        }

        // Department validation
        if (s.getDepartment() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department cannot be empty");
        }

        // Gender validation
        if (s.getGender() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gender cannot be empty");
        }
    }
}




