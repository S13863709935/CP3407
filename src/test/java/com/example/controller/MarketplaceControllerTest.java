package com.example.controller;

import com.example.common.Result;
import com.example.entity.Collect;
import com.example.entity.Comment;
import com.example.entity.Feedback;
import com.example.entity.Goods;
import com.example.entity.Notice;
import com.example.entity.Orders;
import com.example.entity.User;
import com.example.service.CollectService;
import com.example.service.CommentService;
import com.example.service.FeedbackService;
import com.example.service.GoodsService;
import com.example.service.NoticeService;
import com.example.service.OrdersService;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketplaceControllerTest {

    @Mock
    private GoodsService goodsService;
    @Mock
    private CommentService commentService;
    @Mock
    private OrdersService ordersService;
    @Mock
    private UserService userService;
    @Mock
    private CollectService collectService;
    @Mock
    private NoticeService noticeService;
    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private GoodsController goodsController;
    @InjectMocks
    private CommentController commentController;
    @InjectMocks
    private OrdersController ordersController;
    @InjectMocks
    private UserController userController;
    @InjectMocks
    private CollectController collectController;
    @InjectMocks
    private NoticeController noticeController;
    @InjectMocks
    private FeedbackController feedbackController;

    @Test
    void us2PublishesAnItemThroughTheGoodsService() {
        Goods goods = goods("Desk", "Furniture");

        Result result = goodsController.add(goods);

        assertEquals("200", result.getCode());
        verify(goodsService).add(goods);
    }

    @Test
    void us3BrowsesAvailableItemsByCategory() {
        Goods filter = goods(null, "Books");
        PageInfo<Goods> page = pageOf(goods("Algorithms", "Books"));
        when(goodsService.selectFrontPage(filter, 1, 10)).thenReturn(page);

        Result result = goodsController.selectFrontPage(filter, 1, 10);

        assertSame(page, result.getData());
        verify(goodsService).selectFrontPage(filter, 1, 10);
    }

    @Test
    void us4SearchesAvailableItemsByKeyword() {
        Goods filter = goods("camera", null);
        PageInfo<Goods> page = pageOf(goods("Action camera", "Electronics"));
        when(goodsService.selectFrontPage(filter, 1, 10)).thenReturn(page);

        Result result = goodsController.selectFrontPage(filter, 1, 10);

        assertSame(page, result.getData());
        assertEquals("camera", filter.getName());
    }

    @Test
    void us5LoadsDetailsAndAddsAMessage() {
        Goods goods = goods("Bicycle", "Sports");
        goods.setId(5);
        when(goodsService.selectById(5)).thenReturn(goods);
        Comment comment = new Comment();

        Result details = goodsController.selectById(5);
        Result message = commentController.add(comment);

        assertSame(goods, details.getData());
        assertEquals("200", message.getCode());
        verify(commentService).add(comment);
    }

    @Test
    void us6UpdatesListingPublicationStatus() {
        Goods goods = goods("Desk", "Furniture");
        goods.setId(8);
        goods.setSaleStatus("Listed");

        Result result = goodsController.updateById(goods);

        assertEquals("200", result.getCode());
        verify(goodsService).updateById(goods);
    }

    @Test
    void us7LoadsTheSellersListings() {
        Goods filter = new Goods();
        PageInfo<Goods> page = pageOf(goods("Desk", "Furniture"));
        when(goodsService.selectPage(filter, 1, 10)).thenReturn(page);

        Result result = goodsController.selectPage(filter, 1, 10);

        assertSame(page, result.getData());
    }

    @Test
    void us8CreatesAndTracksBuyerAndSellerOrders() {
        Orders order = new Orders();
        order.setStatus("Pending Payment");
        PageInfo<Orders> page = pageOf(order);
        when(ordersService.selectPage(order, 1, 10)).thenReturn(page);
        when(ordersService.selectSalePage(order, 1, 10)).thenReturn(page);

        assertEquals("200", ordersController.add(order).getCode());
        assertSame(page, ordersController.selectPage(order, 1, 10).getData());
        assertSame(page, ordersController.selectSalePage(order, 1, 10).getData());
        verify(ordersService).add(order);
    }

    @Test
    void us9UpdatesTheResidentProfile() {
        User user = new User();
        user.setId(3);
        user.setName("Neighbour");
        user.setAvatar("/files/avatar.png");

        Result result = userController.updateById(user);

        assertEquals("200", result.getCode());
        verify(userService).updateById(user);
    }

    @Test
    void us10AddsAndListsFavouriteItems() {
        Collect collect = new Collect();
        PageInfo<Collect> page = pageOf(collect);
        when(collectService.selectPage(1, 10)).thenReturn(page);

        assertEquals("200", collectController.add(collect).getCode());
        assertSame(page, collectController.selectPage(1, 10).getData());
        verify(collectService).add(collect);
    }

    @Test
    void us11ListsSystemAnnouncements() {
        Notice filter = new Notice();
        PageInfo<Notice> page = pageOf(new Notice());
        when(noticeService.selectPage(filter, 1, 10)).thenReturn(page);

        Result result = noticeController.selectPage(filter, 1, 10);

        assertSame(page, result.getData());
    }

    @Test
    void us12SubmitsFeedbackAndListsReplies() {
        Feedback feedback = new Feedback();
        PageInfo<Feedback> page = pageOf(feedback);
        when(feedbackService.selectPage(feedback, 1, 10)).thenReturn(page);

        assertEquals("200", feedbackController.add(feedback).getCode());
        assertSame(page, feedbackController.selectPage(feedback, 1, 10).getData());
        verify(feedbackService).add(feedback);
    }

    private Goods goods(String name, String category) {
        Goods goods = new Goods();
        goods.setName(name);
        goods.setCategory(category);
        return goods;
    }

    private <T> PageInfo<T> pageOf(T item) {
        return PageInfo.of(Collections.singletonList(item));
    }
}
