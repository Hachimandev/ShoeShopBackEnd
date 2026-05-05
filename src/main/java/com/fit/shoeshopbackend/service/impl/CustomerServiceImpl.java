package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.Customer;
import com.fit.shoeshopbackend.model.OrderStatus;
import com.fit.shoeshopbackend.repository.CustomerRepository;
import com.fit.shoeshopbackend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public int getLoyaltyPointsByUsername(String username) {
        Customer customer = customerRepository.findByAccount_Username(username);
        return customer != null ? customer.getLoyaltyPoints() : 0;
    }

    @Override
    public String getCustomerIdByUsername(String username) {
        Customer customer = findByAccount_Username(username);
        return customer != null ? customer.getCustomerId() : null;
    }

    @Override
    public Customer findByAccount_Username(String username) {
        return customerRepository.findByAccount_Username(username);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Page<Customer> searchCustomers(String search, Double minSpend, Double maxSpend, String startDate, String endDate, Pageable pageable) {
        Page<Customer> page = customerRepository.findAll(pageable);

        List<Customer> filtered = page.getContent().stream()
                .filter(c -> matchesSearch(c, search, minSpend, maxSpend, startDate, endDate))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    private boolean matchesSearch(Customer customer, String search, Double minSpend, Double maxSpend, String startDate, String endDate) {
        // Search by name or customerId
        boolean searchOk = true;
        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            searchOk = (customer.getFullName() != null && customer.getFullName().toLowerCase().contains(s))
                    || (customer.getCustomerId() != null && customer.getCustomerId().toLowerCase().contains(s));
        }

        // Filter by total spending
        boolean spendOk = true;
        if (minSpend != null || maxSpend != null) {
            double spend = customer.getTotalSpending();
            if (minSpend != null && spend < minSpend) spendOk = false;
            if (maxSpend != null && spend > maxSpend) spendOk = false;
        }

        // Filter by registration date
        boolean dateOk = true;
        if ((startDate != null && !startDate.isEmpty()) || (endDate != null && !endDate.isEmpty())) {
            if (customer.getJoinDate() != null) {
                LocalDateTime khDate = customer.getJoinDate();
                if (startDate != null && !startDate.isEmpty()) {
                    LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
                    if (khDate.isBefore(start)) dateOk = false;
                }
                if (endDate != null && !endDate.isEmpty()) {
                    LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
                    if (khDate.isAfter(end)) dateOk = false;
                }
            }
        }

        return searchOk && spendOk && dateOk;
    }

    @Override
    public long countNewCustomersThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);

        return customerRepository.findAll().stream()
                .filter(c -> c.getJoinDate() != null && YearMonth.from(c.getJoinDate()).equals(currentMonth))
                .count();
    }

    @Override
    public double calculateTotalSpendingByCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null || customer.getOrders() == null) return 0;

        return customer.getOrders().stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED)
                .mapToDouble(o -> o.getTotalAmount())
                .sum();
    }
}
