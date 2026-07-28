package com.example.service;

import com.example.common.enums.OrderStatusEnum;
import com.example.entity.Address;
import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.mapper.OrdersMapper;
import com.example.support.AuthenticatedTestSupport;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private OrdersMapper ordersMapper;
    @Mock
    private GoodsService goodsService;
    @Mock
    private AddressService addressService;
    @Mock
    private UserService userService;
    @Mock
    private AdminService adminService;

    @InjectMocks
    private OrdersService ordersService;

    @BeforeEach
    void authenticate() {
        AuthenticatedTestSupport.authenticateResident(userService, adminService, 31);
    }

    @AfterEach
    void cleanThreadState() {
        PageHelper.clearPage();
        AuthenticatedTestSupport.clearAuthentication();
    }

    @Test
    void addBuildsOrderFromTrustedGoodsAddressAndResidentData() {
        Goods goods = new Goods();
        goods.setName("Camera");
        goods.setImg("/files/camera.png");
        goods.setUserId(44);
        goods.setPrice(new BigDecimal("125.50"));
        Address address = new Address();
        address.setName("Resident");
        address.setAddress("1 Community Road");
        address.setPhone("0400000000");
        when(goodsService.selectById(9)).thenReturn(goods);
        when(addressService.selectById(6)).thenReturn(address);
        Orders order = new Orders();
        order.setGoodsId(9);
        order.setAddressId(6);

        ordersService.add(order);

        assertEquals("Camera", order.getGoodsName());
        assertEquals(44, order.getSaleId());
        assertEquals(31, order.getUserId());
        assertEquals(new BigDecimal("125.50"), order.getTotal());
        assertEquals(OrderStatusEnum.NOTPAY.value, order.getStatus());
        assertNotNull(order.getOrderNo());
        assertNotNull(order.getTime());
        verify(ordersMapper).insert(order);
    }

    @Test
    void buyerAndSellerPagesApplyTheAuthenticatedResidentScope() {
        Orders buyerFilter = new Orders();
        Orders sellerFilter = new Orders();
        when(ordersMapper.selectAll(buyerFilter)).thenReturn(Collections.emptyList());
        when(ordersMapper.selectAll(sellerFilter)).thenReturn(Collections.emptyList());

        PageInfo<Orders> buyer = ordersService.selectPage(buyerFilter, 1, 10);
        PageInfo<Orders> seller = ordersService.selectSalePage(sellerFilter, 1, 10);

        assertEquals(31, buyerFilter.getUserId());
        assertEquals(31, sellerFilter.getSaleId());
        assertEquals(0, buyer.getList().size());
        assertEquals(0, seller.getList().size());
    }
}
