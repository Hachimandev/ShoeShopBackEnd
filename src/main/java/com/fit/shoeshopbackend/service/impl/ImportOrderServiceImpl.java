package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.ImportOrder;
import com.fit.shoeshopbackend.repository.ImportOrderRepository;
import com.fit.shoeshopbackend.service.ImportOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImportOrderServiceImpl implements ImportOrderService {

    @Autowired
    private ImportOrderRepository repository;

    @Override
    public List<ImportOrder> getAllImportOrder() {
        return repository.findAll();
    }

    @Override
    public Optional<ImportOrder> getImportOrderById(String importOrderId) {
        return repository.findById(importOrderId);
    }

    @Override
    public ImportOrder addImportOrder(ImportOrder ImportOrder) {
        return repository.save(ImportOrder);
    }

    @Override
    public ImportOrder updateImportOrder(String importOrderId, ImportOrder ImportOrder) {
        ImportOrder.setImportOrderId(importOrderId);
        return repository.save(ImportOrder);
    }

    @Override
    public void deleteImportOrder(String importOrderId) {
        repository.deleteById(importOrderId);
    }
}









