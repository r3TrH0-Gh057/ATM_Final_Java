package com.ATMV2.ATM.service;

import org.springframework.stereotype.Service;
import com.ATMV2.ATM.entity.Account;
import com.ATMV2.ATM.repository.AccountRepository;
import com.ATMV2.ATM.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class Withdrawervice {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final MoventService moventService;

    public String makeWitdraw(String identification, String accountNumber, double amount){
        com.ATMV2.ATM.entity.Customer customer = customerRepository.findByIdentification(identification).orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Account account = accountRepository.findByNumber(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        if(customer.getAccounts().equals(customer)){
            throw new RuntimeException("Account not found for this customer");
        }

        if(customer.isBlocked()){
            throw new RuntimeException("Customer is blocked");
        }
        boolean succesfully = moventService.makeWithdraw(account, amount);
        if (!succesfully) {
            throw new RuntimeException("Insufficient funds");
        }

        return "redirect:ATM/menu?message=Withdraw successful!";
    }

}
