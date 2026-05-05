package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StaffService {
    Page<Staff> search(String search, String department, String status, Pageable pageable);

    List<Staff> searchAll(String search, String department, String status);

    Optional<Staff> findById(String id);

    Staff save(Staff staff);

    Optional<Staff> update(String id, Staff staff);

    void deleteById(String id);
}
