package com.fit.shoeshopbackend.service.impl;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fit.shoeshopbackend.model.NhaCungCap;
import com.fit.shoeshopbackend.repository.NhaCungCapRepository;
import com.fit.shoeshopbackend.service.NhaCungCapService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NhaCungCapServiceImpl implements NhaCungCapService {

    private final NhaCungCapRepository repository;

    @Override
    public List<NhaCungCap> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<NhaCungCap> getNhaCungCapById(String id) {
        return repository.findById(id);
    }

    // phát sinh mã NCC
    private String phatSinhMaNCC() {
        return "NCC" + System.currentTimeMillis();
    }

    @Override
    public NhaCungCap addNhaCungCap(NhaCungCap ncc) {
        ncc.setMaNhaCungCap(phatSinhMaNCC());
        return repository.save(ncc);
    }

    @Override
    public NhaCungCap updateNhaCungCap(String id, NhaCungCap ncc) {
        ncc.setMaNhaCungCap(id);
        return repository.save(ncc);
    }

    @Override
    public void deleteNhaCungCap(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<NhaCungCap> searchSuppliers(String keyword) {
        List<NhaCungCap> allSuppliers = repository.findAll();
        String lowerKeyword = keyword.toLowerCase();

        return allSuppliers.stream()
                .filter(supplier ->
                        supplier.getTenNhaCungCap().toLowerCase().contains(lowerKeyword) ||
                                supplier.getMaNhaCungCap().toLowerCase().contains(lowerKeyword)
                )
                .collect(Collectors.toList());
    }

    @Override
    public byte[] exportToExcel() throws IOException {
        List<NhaCungCap> suppliers = repository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Nhà cung cấp");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã nhà cung cấp");
        headerRow.createCell(1).setCellValue("Tên nhà cung cấp");
        headerRow.createCell(2).setCellValue("Điện thoại");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Địa chỉ");

        // Set column widths
        sheet.setColumnWidth(0, 15 * 256);
        sheet.setColumnWidth(1, 25 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 25 * 256);
        sheet.setColumnWidth(4, 30 * 256);

        // Fill data
        int rowNum = 1;
        for (NhaCungCap supplier : suppliers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(supplier.getMaNhaCungCap());
            row.createCell(1).setCellValue(supplier.getTenNhaCungCap());
            row.createCell(2).setCellValue(supplier.getSdt());
            row.createCell(3).setCellValue(supplier.getEmail());
            row.createCell(4).setCellValue(supplier.getDiaChi());
        }

        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
