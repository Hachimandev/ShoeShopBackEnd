package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();

    Optional<Category> getCategoryById(String id);

    Category addCategory(Category category);

    Category updateCategory(String id, Category category);

    void deleteCategory(String id);
}
