package com.ATMV2.ATM.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ATMV2.ATM.entity.Account;
import com.ATMV2.ATM.entity.Movent;

public interface MoventRepository extends JpaRepository<Movent, Long> {
    List<Movent> findByAccount(Account account);
    List<Movent> findByAccount_NumberOrderByDateDesc(String numberAccount);

}
