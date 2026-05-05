package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.Supplier;
import com.fit.shoeshopbackend.repository.SupplierRepository;
import com.fit.shoeshopbackend.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Optional<Supplier> getSupplierById(String id) {
        return supplierRepository.findById(id);
    }

    private String generateSupplierId() {
        return "SUP" + System.currentTimeMillis();
    }

    @Override
    public Supplier addSupplier(Supplier supplier) {
        if (supplier.getSupplierId() == null || supplier.getSupplierId().isEmpty()) {
            supplier.setSupplierId(generateSupplierId());
        }
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplier(String id, Supplier supplier) {
        supplier.setSupplierId(id);
        return supplierRepository.save(supplier);
    }

    @Override
    public void deleteSupplier(String id) {
        supplierRepository.deleteById(id);
    }

    @Override
    public List<Supplier> searchSuppliers(String keyword) {
        List<Supplier> allSuppliers = supplierRepository.findAll();
        String lowerKeyword = keyword.toLowerCase();

        return allSuppliers.stream()
                .filter(s ->
                        (s.getSupplierName() != null && s.getSupplierName().toLowerCase().contains(lowerKeyword)) ||
                        (s.getSupplierId() != null && s.getSupplierId().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }

    @Override
    public byte[] exportToExcel() throws IOException {
        List<Supplier> suppliers = supplierRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Suppliers");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Supplier ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Phone");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Address");

        int rowNum = 1;
        for (Supplier s : suppliers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getSupplierId());
            row.createCell(1).setCellValue(s.getSupplierName());
            row.createCell(2).setCellValue(s.getPhoneNumber());
            row.createCell(3).setCellValue(s.getEmail());
            row.createCell(4).setCellValue(s.getAddress());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
