package com.ATMV2.ATM.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ATMV2.ATM.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdentification(String indentification);

}
