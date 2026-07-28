package com.example.service;

import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void registersANewResidentWithTheUserRole() {
        Account account = new Account();
        account.setUsername("resident");
        account.setPassword("secure-password");

        userService.register(account);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertEquals("resident", captor.getValue().getUsername());
        assertEquals("secure-password", captor.getValue().getPassword());
        assertEquals("USER", captor.getValue().getRole());
        assertEquals("resident", captor.getValue().getName());
    }

    @Test
    void loginReturnsATimeLimitedTokenForValidCredentials() {
        Account request = new Account();
        request.setUsername("resident");
        request.setPassword("secure-password");
        User stored = new User();
        stored.setId(12);
        stored.setUsername("resident");
        stored.setPassword("secure-password");
        when(userMapper.selectByUsername("resident")).thenReturn(stored);

        Account authenticated = userService.login(request);

        assertEquals(12, authenticated.getId());
        assertNotNull(authenticated.getToken());
    }

    @Test
    void loginRejectsAnIncorrectPassword() {
        Account request = new Account();
        request.setUsername("resident");
        request.setPassword("wrong");
        User stored = new User();
        stored.setPassword("correct");
        when(userMapper.selectByUsername("resident")).thenReturn(stored);

        assertThrows(CustomException.class, () -> userService.login(request));
    }
}
