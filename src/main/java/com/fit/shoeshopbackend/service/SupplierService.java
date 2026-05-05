package com.fit.shoeshopbackend.service;



import com.fit.shoeshopbackend.model.Supplier;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SupplierService {
    List<Supplier> getAll();

    Optional<Supplier> getSupplierById(String id);

    Supplier addSupplier(Supplier ncc);

    Supplier updateSupplier(String id, Supplier ncc);

    void deleteSupplier(String id);

    List<Supplier> searchSuppliers(String keyword);

    byte[] exportToExcel() throws IOException;
}










