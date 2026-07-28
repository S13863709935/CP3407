package com.example.service;

import com.example.entity.Goods;
import com.example.mapper.CollectMapper;
import com.example.mapper.GoodsMapper;
import com.example.mapper.LikesMapper;
import com.example.support.AuthenticatedTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoodsServiceAdminTest {

    @Mock
    private GoodsMapper goodsMapper;
    @Mock
    private LikesMapper likesMapper;
    @Mock
    private CollectMapper collectMapper;
    @Mock
    private UserService userService;
    @Mock
    private AdminService adminService;

    @InjectMocks
    private GoodsService goodsService;

    @BeforeEach
    void authenticate() {
        AuthenticatedTestSupport.authenticateAdmin(userService, adminService, 1);
    }

    @AfterEach
    void cleanThreadState() {
        AuthenticatedTestSupport.clearAuthentication();
    }

    @Test
    void administratorCanUpdateAnyListingForModeration() {
        Goods update = new Goods();
        update.setId(40);
        update.setSaleStatus("Listed");

        goodsService.updateById(update);

        verify(goodsMapper, never()).selectById(40);
        verify(goodsMapper).updateById(update);
    }

    @Test
    void administratorCanDeleteAnyListingForModeration() {
        goodsService.deleteById(41);

        verify(goodsMapper, never()).selectById(41);
        verify(goodsMapper).deleteById(41);
    }
}
