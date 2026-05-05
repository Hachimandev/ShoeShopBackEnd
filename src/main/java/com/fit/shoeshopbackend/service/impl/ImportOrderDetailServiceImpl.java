package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.ImportOrderDetail;
import com.fit.shoeshopbackend.repository.ImportOrderDetailRepository;
import com.fit.shoeshopbackend.service.ImportOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImportOrderDetailServiceImpl implements ImportOrderDetailService {

    private final ImportOrderDetailRepository importOrderDetailRepository;

    @Override
    public List<ImportOrderDetail> getAllImportOrderDetails() {
        return importOrderDetailRepository.findAll();
    }

    @Override
    public Optional<ImportOrderDetail> getImportOrderDetailById(String id) {
        return importOrderDetailRepository.findById(id);
    }

    @Override
    public List<ImportOrderDetail> getImportOrderDetailsByImportOrderId(String importOrderId) {
        return importOrderDetailRepository.findByImportOrder_ImportOrderId(importOrderId);
    }

    @Override
    public ImportOrderDetail addImportOrderDetail(ImportOrderDetail detail) {
        return importOrderDetailRepository.save(detail);
    }

    @Override
    public ImportOrderDetail updateImportOrderDetail(String id, ImportOrderDetail detail) {
        return importOrderDetailRepository.save(detail);
    }

    @Override
    public void deleteImportOrderDetail(String id) {
        importOrderDetailRepository.deleteById(id);
    }
}
