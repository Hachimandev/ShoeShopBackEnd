package com.fit.shoeshopbackend.service;



import com.fit.shoeshopbackend.model.NhaCungCap;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface NhaCungCapService {
    List<NhaCungCap> getAll();

    Optional<NhaCungCap> getNhaCungCapById(String id);

    NhaCungCap addNhaCungCap(NhaCungCap ncc);

    NhaCungCap updateNhaCungCap(String id, NhaCungCap ncc);

    void deleteNhaCungCap(String id);

    List<NhaCungCap> searchSuppliers(String keyword);

    byte[] exportToExcel() throws IOException;
}

