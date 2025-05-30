package com.ATMV2.ATM.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ATMV2.ATM.entity.Account;
import com.ATMV2.ATM.entity.Customer;
import com.ATMV2.ATM.repository.AccountRepository;
import com.ATMV2.ATM.repository.MoventRepository;
import com.ATMV2.ATM.service.AccountService;
import com.ATMV2.ATM.service.CustomerService;
import com.ATMV2.ATM.service.MoventService;
import com.ATMV2.ATM.service.Withdrawervice;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/atm")
public class AtmController {
    private final CustomerService customerservice;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final MoventRepository movementRepository;
    private final Withdrawervice withdrawService;
    private final MoventService moventService;

    @GetMapping
    public String loginForm() {
        return "cajero/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String accountNumber, @RequestParam String pin, HttpSession session, Model model) {
        var account = accountService.searchByNumber(accountNumber);
        if (account.isEmpty()) {
            model.addAttribute("error", "Account not found or does not exist");
            return "cajero/login";
        }

        Customer customer = account.get().getCustomer();
        if (customer.isBlocked()) {
            model.addAttribute("error", "Account is blocked");
            return "cajero/login";
        }

        if (!customer.getPin().equals(pin)) {
            CustomerService.incrementFailedAttempts(customer);
            if (customer.getFailedAttempts() >= 3) {
                customerservice.blockCustomer(customer);
                model.addAttribute("error", "Account is blocked due to too many failed attempts");
            } else {
                model.addAttribute("error", "Invalid PIN");
            }
            return "cajero/login";
        }

        customerservice.reebotAttempts(customer);
        session.setAttribute("customer", customer);
        return "redirect:/atm/menu";
    }

    @GetMapping("/menu")
    public String menu(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/atm";
        }
        model.addAttribute("customer", customer);
        model.addAttribute("accounts", accountService.searchByCustomer(customer));
        return "cajero/menu";
    }

    @GetMapping("/consult")
    public String consults(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/atm";
        }
        model.addAttribute("accounts", accountService.searchByCustomer(customer));
        return "cajero/consultas";
    }

    @GetMapping("/movents/{number}")
    public String movents(@PathVariable String number, Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/atm";
        }
        try {
            Account account = accountRepository.findByNumber(number)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            var movents = movementRepository.findByAccount(account);
            model.addAttribute("movents", movents);

            return "cajero/movimientos";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving movements: " + e.getMessage());
            return "cajero/consultas";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/atm";
    }

    @GetMapping("/withdraw")
public String showFormWithdraw(Model model, HttpSession session) {
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
        return "redirect:/atm";
    }
    model.addAttribute("accounts", accountService.searchByCustomer(customer)); // <-- Añade esto
    return "cajero/retiro";
}

    @PostMapping("/withdraw")
    public String makeWithdraw(@RequestParam String identification,
                               @RequestParam String accountNumber,
                               @RequestParam double amount,
                               RedirectAttributes redirectAttributes) {
        try {
            String result = withdrawService.makeWitdraw(identification, accountNumber, amount);
            redirectAttributes.addFlashAttribute("message", "Withdraw successful: ");
            return result;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error making withdraw: " + e.getMessage());
            return "redirect:/atm/withdraw";
        }
    }

    @GetMapping("/deposit")
    public String showFormDeposit(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/atm";
        }
        return "cajero/consignar";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam String accountNumber, @RequestParam double amount, Model model) {
        try {
            Account account = accountRepository.findByNumber(accountNumber)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            moventService.makeDeposit(account, amount);

            model.addAttribute("message", "Deposit successful. New balance: " + account.getBalance());
        } catch (Exception e) {
            model.addAttribute("error", "Error making deposit: " + e.getMessage());
        }
        return "cajero/consignar";
    }

    @GetMapping("/transfer")
    public String showFormTransfer(Model model) {
        model.addAttribute("transferForm", new TransferForm());
        return "cajero/transferir";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam String destinyAccountNumber,
                          @RequestParam double amount,
                          HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) return "redirect:/atm";
        List<Account> accounts = (List<Account>) accountService.searchByCustomer(customer);
        Account origin = accounts.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No found origin account"));

        try { 
            Account destiny = accountService.searchByNumber(destinyAccountNumber)
                    .orElseThrow(() -> new RuntimeException("Destiny account not found"));
         
        } catch (Exception e) {
            model.addAttribute("error", "Error making transfer: " + e.getMessage());
        }
        return "cajero/transferir";
    }

    @GetMapping("/holder")
    @ResponseBody
    public Map<String, String> getHolder(@RequestParam String number) {
        return accountService.searchByNumber(number)
                .map(account -> Map.of("name", account.getCustomer().getName()))
                .orElse(Map.of());
    }

    @GetMapping("/changePin")
    public String showFormChangePin() {
        return "cajero/cambiar-clave";
    }

    @PostMapping("/changePin")
    public String changePin(@RequestParam String actualPin,
                            @RequestParam String newPin,
                            @RequestParam String confirmPin,
                            HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/atm";
        }
        if (!customer.getPin().equals(actualPin)) {
            model.addAttribute("error", "Actual PIN is incorrect");
            return "cajero/cambiar-clave";
        }
        if (!newPin.equals(confirmPin)) {
            model.addAttribute("error", "New PIN and confirm PIN do not match");
            return "cajero/cambiar-clave";
        }

        customerservice.changePin(customer, newPin);
        session.setAttribute("customer", customer);
        model.addAttribute("message", "PIN changed successfully");
        return "cajero/cambiar-clave";
    }
}