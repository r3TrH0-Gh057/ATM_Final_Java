package com.ATMV2.ATM.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ATMV2.ATM.entity.Account;
import com.ATMV2.ATM.entity.Customer;
import com.ATMV2.ATM.entity.TypeAccount;
import com.ATMV2.ATM.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AccountService {
    private final AccountRepository accountRepository;

    public Account createAccount(Customer customer, String number, TypeAccount type, double initialMoney) {
    Account account = Account.builder()
        .customer(customer) 
        .number(number)
        .type(type)
        .balance(initialMoney)
        .build();
    return accountRepository.save(account);
}

    public Optional<Account> searchByNumber(String numeber) {
        return accountRepository.findByNumber(numeber);
    }

    public double consultBalance(Account account) {
        return account.getBalance();
    }

    public List<Account> getAccountCustomer(Customer customer) {
        return accountRepository.findByCustomer(customer);
    }

    public List<Account> searchByCustomer(Customer customer) {
    return accountRepository.findByCustomer(customer);
}

}