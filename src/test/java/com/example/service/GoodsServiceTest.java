package com.example.service;

import com.example.entity.Collect;
import com.example.entity.Goods;
import com.example.entity.Likes;
import com.example.mapper.CollectMapper;
import com.example.mapper.GoodsMapper;
import com.example.mapper.LikesMapper;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {

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
        AuthenticatedTestSupport.authenticateResident(userService, adminService, 21);
    }

    @AfterEach
    void cleanThreadState() {
        PageHelper.clearPage();
        AuthenticatedTestSupport.clearAuthentication();
    }

    @Test
    void addAppliesOwnershipAuditDefaultsAndCanonicalStatus() {
        Goods goods = new Goods();
        goods.setSaleStatus("On-shelf");

        goodsService.add(goods);

        assertEquals(21, goods.getUserId());
        assertEquals("Listed", goods.getSaleStatus());
        assertEquals(0, goods.getReadCount());
        verify(goodsMapper).insert(goods);
    }

    @Test
    void updateNormalisesLegacyOffShelfValue() {
        Goods goods = new Goods();
        goods.setId(4);
        goods.setSaleStatus("Unlisted");

        goodsService.updateById(goods);

        assertEquals("Off-shelf", goods.getSaleStatus());
        verify(goodsMapper).updateById(goods);
    }

    @Test
    void detailsIncludeLikeAndFavouriteState() {
        Goods goods = new Goods();
        goods.setId(5);
        when(goodsMapper.selectById(5)).thenReturn(goods);
        when(likesMapper.selectByUserIdAndFid(21, 5)).thenReturn(new Likes());
        when(likesMapper.selectCountByFid(5)).thenReturn(3);
        when(collectMapper.selectByUserIdAndFid(21, 5)).thenReturn(null);
        when(collectMapper.selectCountByFid(5)).thenReturn(8);

        Goods result = goodsService.selectById(5);

        assertTrue(result.getUserLikes());
        assertFalse(result.getUserCollect());
        assertEquals(3, result.getLikesCount());
        assertEquals(8, result.getCollectCount());
    }

    @Test
    void marketplacePageIsScopedAndDecoratedWithLikeCounts() {
        Goods filter = new Goods();
        Goods listed = new Goods();
        listed.setId(10);
        when(goodsMapper.selectFrontAll(filter)).thenReturn(Collections.singletonList(listed));
        when(likesMapper.selectCountByFid(10)).thenReturn(2);

        PageInfo<Goods> result = goodsService.selectFrontPage(filter, 1, 10);

        assertEquals(21, filter.getUserId());
        assertEquals(1, result.getList().size());
        assertEquals(2, result.getList().get(0).getLikesCount());
    }
}
