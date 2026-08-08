package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByActiveTrue();
    Optional<Customer> findByDocumentNumber(String documentNumber);
    List<Customer> findByFullNameContainingIgnoreCaseOrPhoneContaining(String name, String phone);
}
