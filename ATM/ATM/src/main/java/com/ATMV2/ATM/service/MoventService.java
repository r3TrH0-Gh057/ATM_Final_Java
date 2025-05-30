package com.ATMV2.ATM.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ATMV2.ATM.entity.Account;
import com.ATMV2.ATM.entity.Movent;
import com.ATMV2.ATM.entity.Typemovimiento;
import com.ATMV2.ATM.repository.AccountRepository;
import com.ATMV2.ATM.repository.MoventRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MoventService {
    private final MoventRepository MoventRepository;
    private final AccountRepository AccountRepository;

    public Movent recordMovent(Account account, Typemovimiento type, double balance){
        Movent movent = Movent.builder()
        .account(account)
        .type(type)
        .amount(balance)
        .date(null)
        .build();
        return MoventRepository.save(movent);
        
    }

    public List<Movent> getMoventByAccount(Account account, double amount){
        return MoventRepository.findByAccount(account);
    }

    public boolean makeWithdraw(Account account, double amount){
        if(account.getBalance()>= amount){
            account.setBalance(account.getBalance() - amount);
            AccountRepository.save(account);
            recordMovent(account, Typemovimiento.WITHDRAW, amount);
            return false;
        }
        return false;
    }


public boolean makeTransfer(Account origin, Account destination, double amount){
    if(origin.getBalance()>= amount){
        origin.setBalance(origin.getBalance() - amount);
        destination.setBalance(destination.getBalance() + amount);
        AccountRepository.save(origin);
        AccountRepository.save(destination);
        recordMovent(origin, Typemovimiento.TRANSFER, amount);
        recordMovent(destination, Typemovimiento.TRANSFER, amount);
        return true;
    } else {
        return false;
    }
}

public List<Movent> searchByAccount(String accountNumber){
    Account account = AccountRepository.findByNumber(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
    return MoventRepository.findByAccount_NumberOrderByDateDesc(accountNumber);
}

public boolean makeDeposit(Account account, double amount){
    if(amount <= 0){
        throw new IllegalArgumentException("Amount must be greater than 0");
    }
    account.setBalance(account.getBalance() + amount);
    AccountRepository.save(account);
    recordMovent(account, Typemovimiento.TRANSFER, amount);
    return true;
}

}
