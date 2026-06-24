package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.Category;
import com.fit.shoeshopbackend.repository.CategoryRepository;
import com.fit.shoeshopbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(String id, Category category) {
        category.setCategoryId(id);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public byte[] exportToExcel() throws IOException {
        List<Category> categories = categoryRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Categories");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Category ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Description");

        int rowNum = 1;
        for (Category c : categories) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getCategoryId());
            row.createCell(1).setCellValue(c.getCategoryName());
            row.createCell(2).setCellValue(c.getDescription());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
