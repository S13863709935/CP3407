package com.example.mapper;

import com.example.entity.Goods;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoodsMapperIntegrationTest {

    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    void frontPageReturnsOnlyApprovedListedItemsOwnedByOtherResidents() {
        Goods filter = residentFilter();

        List<Goods> result = goodsMapper.selectFrontAll(filter);

        assertThat(result)
                .extracting(Goods::getName)
                .containsExactlyInAnyOrder("Camera", "Bookshelf");
        assertThat(result)
                .allSatisfy(goods -> {
                    assertThat(goods.getStatus()).isEqualTo("通过");
                    assertThat(goods.getSaleStatus()).isEqualTo("Listed");
                    assertThat(goods.getUserId()).isNotEqualTo(1);
                    assertThat(goods.getUserName()).isEqualTo("Neighbour");
                });
    }

    @Test
    void categoryFilterReturnsOnlyMatchingAvailableItems() {
        Goods filter = residentFilter();
        filter.setCategory("Furniture");

        List<Goods> result = goodsMapper.selectFrontAll(filter);

        assertThat(result)
                .extracting(Goods::getName)
                .containsExactly("Bookshelf");
    }

    @Test
    void keywordFilterReturnsAnEmptyListWhenNothingMatches() {
        Goods filter = residentFilter();
        filter.setName("non-existent item");

        List<Goods> result = goodsMapper.selectFrontAll(filter);

        assertThat(result).isEmpty();
    }

    private Goods residentFilter() {
        Goods filter = new Goods();
        filter.setUserId(1);
        filter.setSort("最新");
        return filter;
    }
}
