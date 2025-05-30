package com.ATMV2.ATM.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.ATMV2.ATM.entity.Customer;
import com.ATMV2.ATM.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        customer.setBlocked(false);
        customer.setFailedAttempts(0);
        return customerRepository.save(customer);
    }

    public Optional<Customer> searchForIdentification (String indentification){
        return customerRepository.findByIdentification(indentification);
    }

    public boolean checkPin(Customer customer, String pin) {
        if (customer.isBlocked()) return false;
        if (customer.getPin().equals(pin)) {
            customer.setFailedAttempts(0);
            customerRepository.save(customer);
            return true;
        } else {
            int attempts = customer.getFailedAttempts() +1;
            customer.setFailedAttempts(attempts);
            if (attempts >= 3) {
                customer.setBlocked(true);
            }

            customerRepository.save(customer);
            return false;
        }
    }

    public void unlockCustomer(String identification, String newPin) {
    Optional<Customer> customerOpt = customerRepository.findByIdentification(identification);
    if (customerOpt.isPresent()) {
        Customer customer = customerOpt.get();
        customer.setBlocked(false);
        customer.setFailedAttempts(0);
        customer.setPin(newPin);
        customerRepository.save(customer);
    }
}

    public static void incrementFailedAttempts(Customer customer) {
    int attempts = customer.getFailedAttempts() + 1;
    customer.setFailedAttempts(attempts);
    // Si necesitas guardar, hazlo desde donde se llama este método
    }

    public void blockCustomer(Customer customer) {
    customer.setBlocked(true);
    customerRepository.save(customer);
    }

    public void reebotAttempts(Customer customer) {
        customer.setFailedAttempts(0);
        customerRepository.save(customer);
    }

    public void changePin(Customer customer, String newPin) {
        customer.setPin(newPin);
        customerRepository.save(customer);
    }

}