package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.ImportOrderDetail;
import java.util.List;
import java.util.Optional;

public interface ImportOrderDetailService {
    List<ImportOrderDetail> getAllImportOrderDetails();
    Optional<ImportOrderDetail> getImportOrderDetailById(String id);
    List<ImportOrderDetail> getImportOrderDetailsByImportOrderId(String importOrderId);
    ImportOrderDetail addImportOrderDetail(ImportOrderDetail detail);
    ImportOrderDetail updateImportOrderDetail(String id, ImportOrderDetail detail);
    void deleteImportOrderDetail(String id);
}
