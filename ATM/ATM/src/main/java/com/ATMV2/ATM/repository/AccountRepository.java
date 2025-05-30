package com.ATMV2.ATM.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ATMV2.ATM.entity.Account;
import com.ATMV2.ATM.entity.Customer;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String numero);
    List<Account> findByCustomer(Customer customer);


}
