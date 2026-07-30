package com.example.service;

import com.example.entity.Address;
import com.example.mapper.AddressMapper;
import com.example.support.AuthenticatedTestSupport;
import com.github.pagehelper.PageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressMapper addressMapper;
    @Mock
    private UserService userService;
    @Mock
    private AdminService adminService;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void authenticate() {
        AuthenticatedTestSupport.authenticateResident(userService, adminService, 73);
    }

    @AfterEach
    void cleanThreadState() {
        PageHelper.clearPage();
        AuthenticatedTestSupport.clearAuthentication();
    }

    @Test
    void addressCreationAlwaysUsesTheAuthenticatedResident() {
        Address address = new Address();
        address.setUserId(999);

        addressService.add(address);

        assertEquals(73, address.getUserId());
        verify(addressMapper).insert(address);
    }

    @Test
    void residentAddressListOverridesAForgedOwnerFilter() {
        Address filter = new Address();
        filter.setUserId(999);
        when(addressMapper.selectAll(filter)).thenReturn(Collections.emptyList());

        addressService.selectAll(filter);

        assertEquals(73, filter.getUserId());
        verify(addressMapper).selectAll(filter);
    }

    @Test
    void residentAddressPageIsScopedToTheAuthenticatedAccount() {
        Address filter = new Address();
        when(addressMapper.selectAll(filter)).thenReturn(Collections.emptyList());

        assertEquals(0, addressService.selectPage(filter, 1, 10).getList().size());
        assertEquals(73, filter.getUserId());
    }

    @Test
    void administratorAddressListRemainsUnscoped() {
        AuthenticatedTestSupport.clearAuthentication();
        AuthenticatedTestSupport.authenticateAdmin(userService, adminService, 5);
        Address filter = new Address();
        when(addressMapper.selectAll(filter)).thenReturn(Collections.emptyList());

        addressService.selectAll(filter);

        assertNull(filter.getUserId());
    }

    @Test
    void addressQueriesAndUpdateDelegateToTheMapper() {
        Address address = new Address();
        address.setId(9);
        when(addressMapper.selectById(9)).thenReturn(address);

        assertSame(address, addressService.selectById(9));
        addressService.updateById(address);

        verify(addressMapper).updateById(address);
    }

    @Test
    void addressBatchDeleteRemovesEverySelectedAddress() {
        addressService.deleteBatch(Arrays.asList(9, 10));

        verify(addressMapper).deleteById(9);
        verify(addressMapper).deleteById(10);
    }
}
