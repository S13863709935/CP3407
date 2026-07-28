package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.common.enums.ListingStatusEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.StatusEnum;
import com.example.entity.Account;
import com.example.entity.Collect;
import com.example.entity.Goods;
import com.example.entity.Likes;
import com.example.exception.CustomException;
import com.example.mapper.CollectMapper;
import com.example.mapper.GoodsMapper;
import com.example.mapper.LikesMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

/**
 * 二手商品业务处理
 **/
@Service
public class GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    LikesMapper likesMapper;
    @Resource
    CollectMapper collectMapper;

    /**
     * 新增
     */
    public void add(Goods goods) {
        goods.setDate(DateUtil.today());
        Account currentUser = TokenUtils.getCurrentUser();
        goods.setUserId(currentUser.getId());
        goods.setStatus(StatusEnum.NOT_AUDIT.value);
        String saleStatus = ListingStatusEnum.normalise(goods.getSaleStatus());
        goods.setSaleStatus(saleStatus == null ? ListingStatusEnum.OFF_SHELF.value : saleStatus);
        goods.setReadCount(0);
        goodsMapper.insert(goods);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        assertCanManageListing(id, TokenUtils.getCurrentUser());
        goodsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        Account currentUser = TokenUtils.getCurrentUser();
        for (Integer id : ids) {
            assertCanManageListing(id, currentUser);
        }
        for (Integer id : ids) {
            goodsMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Goods goods) {
        Account currentUser = TokenUtils.getCurrentUser();
        assertCanManageListing(goods.getId(), currentUser);
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            goods.setStatus(StatusEnum.NOT_AUDIT.value);
        }
        if (goods.getSaleStatus() != null) {
            goods.setSaleStatus(ListingStatusEnum.normalise(goods.getSaleStatus()));
        }
        goodsMapper.updateById(goods);
    }

    /**
     * 根据ID查询
     */
    public Goods selectById(Integer id) {
        Goods goods = goodsMapper.selectById(id);

        Account currentUser = TokenUtils.getCurrentUser();
        Likes likes = likesMapper.selectByUserIdAndFid(currentUser.getId(), id);
        goods.setUserLikes(likes != null);
        int likesCount = likesMapper.selectCountByFid(id);
        goods.setLikesCount(likesCount);

        Collect collect = collectMapper.selectByUserIdAndFid(currentUser.getId(), id);
        goods.setUserCollect(collect != null);
        int collectCount = collectMapper.selectCountByFid(id);
        goods.setCollectCount(collectCount);

        return goods;
    }

    /**
     * 查询所有
     */
    public List<Goods> selectAll(Goods goods) {
        return goodsMapper.selectAll(goods);
    }

    /**
     * 分页查询
     */
    public PageInfo<Goods> selectPage(Goods goods, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            goods.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectAll(goods);
        return PageInfo.of(list);
    }

    public PageInfo<Goods> selectFrontPage(Goods goods, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            goods.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectFrontAll(goods);
        for (Goods g : list) {
            int likesCount = likesMapper.selectCountByFid(g.getId());
            g.setLikesCount(likesCount);
        }
        return PageInfo.of(list);
    }

    public void updateReadCount(Integer id) {
        goodsMapper.updateReadCount(id);
    }

    private void assertCanManageListing(Integer listingId, Account currentUser) {
        if (currentUser != null && RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            return;
        }
        if (currentUser == null
                || !RoleEnum.USER.name().equals(currentUser.getRole())
                || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }

        Goods existing = goodsMapper.selectById(listingId);
        if (existing == null || !Objects.equals(existing.getUserId(), currentUser.getId())) {
            throw new CustomException(ResultCodeEnum.FORBIDDEN_ERROR);
        }
    }
}
