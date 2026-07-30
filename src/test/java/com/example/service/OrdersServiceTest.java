package com.example.service;

import com.example.common.enums.OrderStatusEnum;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
        assertEquals(20, order.getOrderNo().length());
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

    @Test
    void administratorOrderPagesDoNotApplyAResidentScope() {
        AuthenticatedTestSupport.clearAuthentication();
        AuthenticatedTestSupport.authenticateAdmin(userService, adminService, 8);
        Orders buyerFilter = new Orders();
        Orders sellerFilter = new Orders();
        when(ordersMapper.selectAll(buyerFilter)).thenReturn(Collections.emptyList());
        when(ordersMapper.selectAll(sellerFilter)).thenReturn(Collections.emptyList());

        ordersService.selectPage(buyerFilter, 1, 10);
        ordersService.selectSalePage(sellerFilter, 1, 10);

        assertEquals(null, buyerFilter.getUserId());
        assertEquals(null, sellerFilter.getSaleId());
    }

    @Test
    void orderQueriesAndUpdatesDelegateToTheMapper() {
        Orders order = new Orders();
        order.setId(17);
        order.setOrderNo("order-17");
        when(ordersMapper.selectById(17)).thenReturn(order);
        when(ordersMapper.selectByOrderNo("order-17")).thenReturn(order);

        assertSame(order, ordersService.selectById(17));
        assertSame(order, ordersService.selectByOrderNo("order-17"));

        ordersService.updateById(order);
        ordersService.deleteById(17);

        verify(ordersMapper).updateById(order);
        verify(ordersMapper).deleteById(17);
    }

    @Test
    void batchDeleteRemovesEverySelectedOrder() {
        ordersService.deleteBatch(Arrays.asList(17, 18));

        verify(ordersMapper).deleteById(17);
        verify(ordersMapper).deleteById(18);
    }

    @Test
    void salesBarIncludesOnlyCompletedOrdersAndAggregatesBySeller() {
        Orders first = completedOrder("Neighbour A", "10.00", DateUtil.today());
        Orders second = completedOrder("Neighbour A", "15.50", DateUtil.today());
        Orders pending = new Orders();
        pending.setStatus(OrderStatusEnum.NOTPAY.value);
        pending.setSaleName("Neighbour B");
        pending.setTotal(new BigDecimal("99.00"));
        when(ordersMapper.selectAll(null)).thenReturn(Arrays.asList(first, second, pending));

        List<Dict> bars = ordersService.selectBar();

        assertEquals(1, bars.size());
        assertEquals("Neighbour A", bars.get(0).get("name"));
        assertEquals(new BigDecimal("25.50"), bars.get(0).get("value"));
    }

    @Test
    void salesLineUsesCompletedOrdersFromTheLastSevenDays() {
        String yesterday = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -1));
        Orders completed = completedOrder("Neighbour A", "23.40", yesterday + " 12:00:00");
        Orders cancelled = new Orders();
        cancelled.setStatus(OrderStatusEnum.CANCEL.value);
        cancelled.setTime(yesterday + " 13:00:00");
        cancelled.setTotal(new BigDecimal("100.00"));
        when(ordersMapper.selectAll(null)).thenReturn(Arrays.asList(completed, cancelled));

        List<Dict> line = ordersService.selectLine();

        assertEquals(8, line.size());
        assertFalse(line.stream().noneMatch(point ->
                yesterday.equals(point.get("name"))
                        && new BigDecimal("23.40").equals(point.get("value"))));
    }

    private Orders completedOrder(String seller, String total, String time) {
        Orders order = new Orders();
        order.setStatus(OrderStatusEnum.DONE.value);
        order.setSaleName(seller);
        order.setTotal(new BigDecimal(total));
        order.setTime(time);
        return order;
    }
}
