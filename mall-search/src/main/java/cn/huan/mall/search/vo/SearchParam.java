package cn.huan.mall.search.vo;

import lombok.Data;

import java.util.List;

/*
    http://search.mall.com/list.html?catalog3Id=165
 */
@Data
public class SearchParam {
    //检索关键字(检索条件)
    private String keyword;
    //三级分类id(过滤条件)
    private Long catalog3Id;
    /**
     * 排序条件
     * sort=saleCount_asc/desc  销量排序
     * sort=skuPrice_asc/desc  商品价格排序
     * sort=hotScore_asc/desc  热度评分排序
     */
    private String sort;
    /**
     * 过滤条件
     * hasStock(是否有货) skuPrice()价格区间 brandId(品牌Id，可多选) attrs(属性，可多选)
     * hasStock=0/1 (无货/有货)
     * skuPrice=1_500/_500/500_
     * brandId=1&brandId=2...
     * attrs=1_属性值:属性值&2_属性值:属性值...
     */
    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandId;
    private List<String> attrs;
    //当前页码(默认第一页)
    private Integer pageNum = 1;

}
