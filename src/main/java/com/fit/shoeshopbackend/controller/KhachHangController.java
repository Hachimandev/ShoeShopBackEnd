package com.fit.shoeshopbackend.controller;




import com.fit.shoeshopbackend.dto.KhachHangDTO;
import com.fit.shoeshopbackend.dto.UpdateKhachHangDTO;
import com.fit.shoeshopbackend.model.KhachHang;
import com.fit.shoeshopbackend.service.KhachHangService;
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
@RequestMapping("/api/khachhang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhachHangController {

    private final KhachHangService khachHangService;

    @GetMapping("/diem/{username}")
    public int layDiemTichLuy(@PathVariable String username) {
        return khachHangService.layDiemTichLuyTheoUsername(username);
    }

    @GetMapping("/{username}")
    public KhachHang getKhachHang(@PathVariable String username) {
        return khachHangService.findByTaiKhoan_TenDangNhap(username);
    }

    @GetMapping("/info/{username}")
    public KhachHangDTO layThongTinKhachHang(@PathVariable String username) {
        KhachHang kh = khachHangService.findByTaiKhoan_TenDangNhap(username);
        if (kh == null) return null;

        String diaChiChiTiet = "";
        String phuongXa = "";
        String quanHuyen = "";
        String tinhThanh = "";

        if (kh.getDiaChi() != null && !kh.getDiaChi().isBlank()) {
            String[] parts = kh.getDiaChi().split(",");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            if (parts.length >= 4) {
                diaChiChiTiet = parts[0];
                phuongXa = parts[1];
                quanHuyen = parts[2];
                tinhThanh = parts[3];
            } else if (parts.length == 3) {
                phuongXa = parts[0];
                quanHuyen = parts[1];
                tinhThanh = parts[2];
            } else if (parts.length == 2) {
                quanHuyen = parts[0];
                tinhThanh = parts[1];
            } else if (parts.length == 1) {
                tinhThanh = parts[0];
            }
        }

        return new KhachHangDTO(
                kh.getHoTen(),
                kh.getEmail(),
                kh.getSdt(),
                diaChiChiTiet,
                phuongXa,
                quanHuyen,
                tinhThanh
        );
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<?> capNhatThongTinKhachHang(
            @PathVariable String username,
            @RequestBody UpdateKhachHangDTO dto
    ) {
        KhachHang kh = khachHangService.findByTaiKhoan_TenDangNhap(username);
        if (kh == null) return ResponseEntity.notFound().build();

        if (dto.getHoTen() != null) kh.setHoTen(dto.getHoTen());
        if (dto.getEmail() != null) kh.setEmail(dto.getEmail());
        if (dto.getSdt() != null) kh.setSdt(dto.getSdt());
        if (dto.getDiaChi() != null && !dto.getDiaChi().isBlank()) kh.setDiaChi(dto.getDiaChi());

        khachHangService.save(kh);
        return ResponseEntity.ok(kh);
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<KhachHang>> getAllCustomers() {
        List<KhachHang> customers = khachHangService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<KhachHang>> searchCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minSpend,
            @RequestParam(required = false) Double maxSpend,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<KhachHang> result = khachHangService.searchCustomers(
                search, minSpend, maxSpend, startDate, endDate,
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats/new-this-month")
    public ResponseEntity<Long> getNewCustomersThisMonth() {
        long count = khachHangService.countNewCustomersThisMonth();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/total-count")
    public ResponseEntity<Long> getTotalCustomerCount() {
        long count = khachHangService.getAllCustomers().size();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportCustomersToExcel() {
        try {
            List<KhachHang> customers = khachHangService.getAllCustomers();
            byte[] excelFile = generateExcelFile(customers);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=khach_hang.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] generateExcelFile(List<KhachHang> customers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Khách Hàng");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã Khách Hàng", "Họ Tên", "Email", "Số Điện Thoại", "Tổng Chi Tiêu", "Ngày Tham Gia"};
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

        // Create data rows
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        int rowNum = 1;
        for (KhachHang customer : customers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(customer.getMaKhachHang());
            row.createCell(1).setCellValue(customer.getHoTen());
            row.createCell(2).setCellValue(customer.getEmail());
            row.createCell(3).setCellValue(customer.getSdt());
            row.createCell(4).setCellValue(customer.getTongChiTieu());
            if (customer.getNgayThamGia() != null) {
                row.createCell(5).setCellValue(customer.getNgayThamGia().format(dateFormatter));
            } else {
                row.createCell(5).setCellValue("");
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

}

