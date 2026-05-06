package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.ImportOrder;
import java.util.List;
import java.util.Optional;

public interface ImportOrderService {
    List<ImportOrder> getAllImportOrder();
    Optional<ImportOrder> getImportOrderById(String importOrderId);
    ImportOrder addImportOrder(ImportOrder ImportOrder);
    ImportOrder updateImportOrder(String importOrderId, ImportOrder ImportOrder);
    void deleteImportOrder(String importOrderId);
}









