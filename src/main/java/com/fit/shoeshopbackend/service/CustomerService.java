package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    int getLoyaltyPointsByUsername(String username);
    Customer findByAccount_Username(String username);
    Customer save(Customer kh);
    String getCustomerIdByUsername(String username);
    List<Customer> getAllCustomers();
    Page<Customer> searchCustomers(String search, Double minSpend, Double maxSpend, String startDate, String endDate, Pageable pageable);
    long countNewCustomersThisMonth();
    double calculateTotalSpendingByCustomer(String customerId);
}










