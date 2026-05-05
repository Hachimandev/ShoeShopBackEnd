package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByAccount_Username(String username);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByAccount_AccountId(String accountId);
}
