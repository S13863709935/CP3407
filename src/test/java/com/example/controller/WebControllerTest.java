package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.service.AdminService;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private UserService userService;

    @InjectMocks
    private WebController controller;

    @Test
    void loginReturnsAuthenticatedUserForValidResidentCredentials() {
        Account request = account("resident", "password", "USER");
        User authenticated = new User();
        authenticated.setId(7);
        authenticated.setUsername("resident");
        authenticated.setRole("USER");
        when(userService.login(any(Account.class))).thenReturn(authenticated);

        Result result = controller.login(request);

        assertEquals("200", result.getCode());
        assertSame(authenticated, result.getData());
        verify(userService).login(request);
    }

    @Test
    void loginRejectsMissingCredentialsBeforeCallingAService() {
        Account request = account("", "", "USER");

        Result result = controller.login(request);

        assertEquals("4001", result.getCode());
        verify(userService, never()).login(any(Account.class));
        verify(adminService, never()).login(any(Account.class));
    }

    @Test
    void registrationDelegatesToResidentService() {
        Account request = account("new-resident", "strong-password", "USER");

        Result result = controller.register(request);

        assertEquals("200", result.getCode());
        verify(userService).register(request);
    }

    private Account account(String username, String password, String role) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(role);
        return account;
    }
}
