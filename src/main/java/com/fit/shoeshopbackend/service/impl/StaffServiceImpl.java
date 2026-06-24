package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.Staff;
import com.fit.shoeshopbackend.repository.StaffRepository;
import com.fit.shoeshopbackend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    @Override
    public Page<Staff> search(String search, String department, String status, Pageable pageable) {
        Page<Staff> page = staffRepository.findAll(pageable);

        List<Staff> filtered = page.getContent().stream()
                .filter(s -> matches(s, search, department, status))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public List<Staff> searchAll(String search, String department, String status) {
        return staffRepository.findAll().stream()
                .filter(s -> matches(s, search, department, status))
                .collect(Collectors.toList());
    }

    private boolean matches(Staff s, String search, String department, String status) {
        // Search by name or staffId
        boolean searchOk = true;
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = search.trim().toLowerCase();
            searchOk = (s.getFullName() != null && s.getFullName().toLowerCase().contains(searchTerm))
                    || (s.getStaffId() != null && s.getStaffId().toLowerCase().contains(searchTerm));
        }

        // Filter by Department
        boolean departmentOk = true;
        if (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("all")) {
            departmentOk = s.getDepartment() != null && s.getDepartment().name().equalsIgnoreCase(department.trim());
        }

        // Filter by Status
        boolean statusOk = true;
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("all")) {
            statusOk = s.getWorkStatus() != null && s.getWorkStatus().name().equalsIgnoreCase(status.trim());
        }

        return searchOk && departmentOk && statusOk;
    }

    @Override
    public Optional<Staff> findById(String id) {
        return staffRepository.findById(id);
    }

    @Override
    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    public Optional<Staff> update(String id, Staff staff) {
        return staffRepository.findById(id).map(existing -> {
            existing.setFullName(staff.getFullName());
            existing.setPhoneNumber(staff.getPhoneNumber());
            existing.setCitizenId(staff.getCitizenId());
            existing.setBirthDate(staff.getBirthDate());
            existing.setEmail(staff.getEmail());
            existing.setImg(staff.getImg());
            existing.setPosition(staff.getPosition());
            existing.setDepartment(staff.getDepartment());
            existing.setGender(staff.getGender());
            existing.setWorkStatus(staff.getWorkStatus());
            return staffRepository.save(existing);
        });
    }

    @Override
    public void deleteById(String id) {
        staffRepository.deleteById(id);
    }

    @Override
    public byte[] exportToExcel() throws IOException {
        List<Staff> staffs = staffRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Staff");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Staff ID");
        headerRow.createCell(1).setCellValue("Full Name");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Phone");
        headerRow.createCell(4).setCellValue("Department");
        headerRow.createCell(5).setCellValue("Position");
        headerRow.createCell(6).setCellValue("Work Status");

        int rowNum = 1;
        for (Staff s : staffs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getStaffId());
            row.createCell(1).setCellValue(s.getFullName());
            row.createCell(2).setCellValue(s.getEmail());
            row.createCell(3).setCellValue(s.getPhoneNumber());
            row.createCell(4).setCellValue(s.getDepartment() != null ? s.getDepartment().name() : "");
            row.createCell(5).setCellValue(s.getPosition() != null ? s.getPosition().name() : "");
            row.createCell(6).setCellValue(s.getWorkStatus() != null ? s.getWorkStatus().name() : "");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
