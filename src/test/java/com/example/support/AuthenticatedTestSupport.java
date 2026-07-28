package com.example.support;

import com.example.common.Constants;
import com.example.entity.User;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.when;

public final class AuthenticatedTestSupport {

    private AuthenticatedTestSupport() {
    }

    public static User authenticateResident(UserService userService,
                                            AdminService adminService,
                                            int userId) {
        User resident = new User();
        resident.setId(userId);
        resident.setUsername("resident-" + userId);
        resident.setPassword("test-signing-password");
        resident.setRole("USER");
        when(userService.selectById(userId)).thenReturn(resident);

        TokenUtils tokenUtils = new TokenUtils();
        ReflectionTestUtils.setField(tokenUtils, "userService", userService);
        ReflectionTestUtils.setField(tokenUtils, "adminService", adminService);
        tokenUtils.setUserService();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.TOKEN,
                TokenUtils.createToken(userId + "-USER", resident.getPassword()));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        return resident;
    }

    public static void clearAuthentication() {
        RequestContextHolder.resetRequestAttributes();
    }
}
