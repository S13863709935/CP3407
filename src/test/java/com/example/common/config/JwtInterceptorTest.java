package com.example.common.config;

import com.example.common.Constants;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtInterceptorTest {

    @Mock
    private UserService userService;
    @Mock
    private AdminService adminService;

    private JwtInterceptor interceptor;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new JwtInterceptor();
        ReflectionTestUtils.setField(interceptor, "userService", userService);
        ReflectionTestUtils.setField(interceptor, "adminService", adminService);
        response = new MockHttpServletResponse();
    }

    @Test
    void missingTokenIsRejected() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        CustomException error = assertThrows(CustomException.class,
                () -> interceptor.preHandle(request, response, new Object()));

        assertEquals("401", error.getCode());
    }

    @Test
    void validResidentTokenFromHeaderIsAccepted() {
        User resident = resident(41, "resident-secret");
        when(userService.selectById(41)).thenReturn(resident);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.TOKEN,
                TokenUtils.createToken("41-USER", resident.getPassword()));

        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void validAdministratorTokenFromRequestParameterIsAccepted() {
        Admin admin = new Admin();
        admin.setId(7);
        admin.setPassword("admin-secret");
        admin.setRole("ADMIN");
        when(adminService.selectById(7)).thenReturn(admin);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(Constants.TOKEN,
                TokenUtils.createToken("7-ADMIN", admin.getPassword()));

        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void malformedTokenIsRejected() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.TOKEN, "not-a-jwt");

        CustomException error = assertThrows(CustomException.class,
                () -> interceptor.preHandle(request, response, new Object()));

        assertEquals("401", error.getCode());
    }

    @Test
    void tokenForUnknownRoleIsRejectedAsUnknownAccount() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.TOKEN,
                TokenUtils.createToken("9-GUEST", "guest-secret"));

        CustomException error = assertThrows(CustomException.class,
                () -> interceptor.preHandle(request, response, new Object()));

        assertEquals("5004", error.getCode());
    }

    @Test
    void tokenWithWrongSignatureIsRejected() {
        User resident = resident(41, "stored-secret");
        when(userService.selectById(41)).thenReturn(resident);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.TOKEN,
                TokenUtils.createToken("41-USER", "different-secret"));

        CustomException error = assertThrows(CustomException.class,
                () -> interceptor.preHandle(request, response, new Object()));

        assertEquals("401", error.getCode());
    }

    private User resident(int id, String password) {
        User resident = new User();
        resident.setId(id);
        resident.setPassword(password);
        resident.setRole("USER");
        return resident;
    }
}
