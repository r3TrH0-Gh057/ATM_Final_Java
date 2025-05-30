package com.ATMV2.ATM.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import com.ATMV2.ATM.entity.Customer;
import com.ATMV2.ATM.entity.TypeAccount;
import com.ATMV2.ATM.service.AccountService;
import com.ATMV2.ATM.service.CustomerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


@Controller
@RequestMapping("/admin")

public class AdminController {
    private final CustomerService customerService;
    private final AccountService accountService;

    public AdminController(CustomerService customerService, AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
    }

    @GetMapping
    public String adminHome(){
        return "admin/index";
    }

    @GetMapping("/create-customer")
    public String showCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/create-customer";
    }

    @PostMapping("/create-customer")
    public String createCustomer(@ModelAttribute Customer customer) {
        customerService.createCustomer(customer);
        return "redirect:/admin";
        
    }

     @GetMapping("/create-account")
    public String showFormAccount(Model model) {
        model.addAttribute("account", new Customer());
        return "admin/create-account";
        
    }
    
    @PostMapping("/create-account")
    public String createAccount(@RequestParam String identification,
                            @RequestParam String number,
                            @RequestParam TypeAccount typeAccount,
                            @RequestParam double balance) {
    Customer customer = customerService.searchForIdentification(identification)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

    accountService.createAccount(customer, number, typeAccount, balance);
    return "redirect:/admin";
}

    @GetMapping("/unlock")
    public String showUnlock() {
        return "admin/unlock";
    }

    @PostMapping("/unlock")
    public String unlockAccount(@RequestParam String identification,
                               @RequestParam String newPin) {
        customerService.unlockCustomer(identification, newPin);
        return "redirect:/admin";
    }


}