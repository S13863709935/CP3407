package com.example.support;

import com.example.common.Constants;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.lenient;

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
        lenient().when(userService.selectById(userId)).thenReturn(resident);

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

    public static Admin authenticateAdmin(UserService userService,
                                          AdminService adminService,
                                          int adminId) {
        Admin administrator = new Admin();
        administrator.setId(adminId);
        administrator.setUsername("admin-" + adminId);
        administrator.setPassword("test-signing-password");
        administrator.setRole("ADMIN");
        lenient().when(adminService.selectById(adminId)).thenReturn(administrator);

        TokenUtils tokenUtils = new TokenUtils();
        ReflectionTestUtils.setField(tokenUtils, "userService", userService);
        ReflectionTestUtils.setField(tokenUtils, "adminService", adminService);
        tokenUtils.setUserService();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.TOKEN,
                TokenUtils.createToken(adminId + "-ADMIN", administrator.getPassword()));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        return administrator;
    }

    public static void clearAuthentication() {
        RequestContextHolder.resetRequestAttributes();
    }
}
