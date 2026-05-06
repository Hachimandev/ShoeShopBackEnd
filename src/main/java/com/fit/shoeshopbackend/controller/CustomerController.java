package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.CustomerDTO;
import com.fit.shoeshopbackend.dto.UpdateCustomerDTO;
import com.fit.shoeshopbackend.model.Customer;
import com.fit.shoeshopbackend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/points/{username:.+}")
    public int getLoyaltyPoints(@PathVariable String username) {
        return customerService.getLoyaltyPointsByUsername(username);
    }

    @GetMapping("/{username:.+}")
    public Customer getCustomer(@PathVariable String username) {
        return customerService.findByAccount_Username(username);
    }

    @GetMapping("/info/{username:.+}")
    public CustomerDTO getCustomerInfo(@PathVariable String username) {
        Customer customer = customerService.findByAccount_Username(username);
        if (customer == null) return null;

        String detailedAddress = "";
        String ward = "";
        String district = "";
        String city = "";

        if (customer.getAddress() != null && !customer.getAddress().isBlank()) {
            String[] parts = customer.getAddress().split(",");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            if (parts.length >= 4) {
                detailedAddress = parts[0];
                ward = parts[1];
                district = parts[2];
                city = parts[3];
            } else if (parts.length == 3) {
                ward = parts[0];
                district = parts[1];
                city = parts[2];
            } else if (parts.length == 2) {
                district = parts[0];
                city = parts[1];
            } else if (parts.length == 1) {
                city = parts[0];
            }
        }

        return new CustomerDTO(
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                detailedAddress,
                ward,
                district,
                city
        );
    }

    @PutMapping("/update/{username:.+}")
    public ResponseEntity<?> updateCustomerInfo(
            @PathVariable String username,
            @RequestBody UpdateCustomerDTO dto
    ) {
        Customer customer = customerService.findByAccount_Username(username);
        if (customer == null) return ResponseEntity.notFound().build();

        if (dto.getFullName() != null) customer.setFullName(dto.getFullName());
        if (dto.getEmail() != null) customer.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) customer.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) customer.setAddress(dto.getAddress());

        customerService.save(customer);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Customer>> searchCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minSpend,
            @RequestParam(required = false) Double maxSpend,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Customer> result = customerService.searchCustomers(
                search, minSpend, maxSpend, startDate, endDate,
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats/new-this-month")
    public ResponseEntity<Long> getNewCustomersThisMonth() {
        long count = customerService.countNewCustomersThisMonth();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/total-count")
    public ResponseEntity<Long> getTotalCustomerCount() {
        long count = customerService.getAllCustomers().size();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportCustomersToExcel() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            byte[] excelFile = generateExcelFile(customers);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] generateExcelFile(List<Customer> customers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Customers");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Customer ID", "Full Name", "Email", "Phone Number", "Total Spending", "Join Date"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (Customer customer : customers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(customer.getCustomerId());
            row.createCell(1).setCellValue(customer.getFullName());
            row.createCell(2).setCellValue(customer.getEmail());
            row.createCell(3).setCellValue(customer.getPhoneNumber());
            row.createCell(4).setCellValue(customer.getTotalSpending());
            if (customer.getJoinDate() != null) {
                row.createCell(5).setCellValue(customer.getJoinDate().format(dateFormatter));
            } else {
                row.createCell(5).setCellValue("");
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
