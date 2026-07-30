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

    @Test
    void loginRejectsAnUnknownUsername() {
        Account request = new Account();
        request.setUsername("missing");
        request.setPassword("anything");
        when(userMapper.selectByUsername("missing")).thenReturn(null);

        assertThrows(CustomException.class, () -> userService.login(request));
    }

    @Test
    void addRejectsADuplicateUsername() {
        User request = new User();
        request.setUsername("resident");
        when(userMapper.selectByUsername("resident")).thenReturn(new User());

        assertThrows(CustomException.class, () -> userService.add(request));
    }

    @Test
    void administratorCreatedResidentReceivesSafeDefaults() {
        User request = new User();
        request.setUsername("new-resident");
        when(userMapper.selectByUsername("new-resident")).thenReturn(null);

        userService.add(request);

        assertEquals("new-resident", request.getName());
        assertEquals("123", request.getPassword());
        assertEquals("USER", request.getRole());
        verify(userMapper).insert(request);
    }

    @Test
    void passwordChangeVerifiesCurrentPasswordBeforeSaving() {
        Account request = new Account();
        request.setUsername("resident");
        request.setPassword("old-password");
        request.setNewPassword("new-password");
        User stored = new User();
        stored.setUsername("resident");
        stored.setPassword("old-password");
        when(userMapper.selectByUsername("resident")).thenReturn(stored);

        userService.updatePassword(request);

        assertEquals("new-password", stored.getPassword());
        verify(userMapper).updateById(stored);
    }

    @Test
    void passwordChangeRejectsAnIncorrectCurrentPassword() {
        Account request = new Account();
        request.setUsername("resident");
        request.setPassword("wrong-password");
        request.setNewPassword("new-password");
        User stored = new User();
        stored.setPassword("old-password");
        when(userMapper.selectByUsername("resident")).thenReturn(stored);

        assertThrows(CustomException.class, () -> userService.updatePassword(request));
    }

    @Test
    void passwordChangeRejectsAnUnknownUsername() {
        Account request = new Account();
        request.setUsername("missing");
        when(userMapper.selectByUsername("missing")).thenReturn(null);

        assertThrows(CustomException.class, () -> userService.updatePassword(request));
    }

    @Test
    void profileUpdateDelegatesThePersistedResidentData() {
        User profile = new User();
        profile.setId(12);
        profile.setName("Updated Neighbour");
        profile.setAvatar("/files/updated.png");

        userService.updateById(profile);

        verify(userMapper).updateById(profile);
    }
}
