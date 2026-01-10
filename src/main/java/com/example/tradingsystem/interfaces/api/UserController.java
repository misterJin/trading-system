package com.example.tradingsystem.interfaces.api;

import com.example.tradingsystem.application.UserAccountService;
import com.example.tradingsystem.domain.user.UserAccount;
import com.example.tradingsystem.interfaces.api.dto.DepositRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAccountService userAccountService;

    public UserController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/{username}/deposit")
    public ResponseEntity<UserAccount> deposit(@PathVariable String username,
                                               @Valid @RequestBody DepositRequest request) {
        UserAccount account = userAccountService.deposit(username, request.getAmount());
        return ResponseEntity.ok(account);
    }
}


