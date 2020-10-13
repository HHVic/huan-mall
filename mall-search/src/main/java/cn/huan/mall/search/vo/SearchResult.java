package cn.huan.mall.search.vo;

import cn.huan.common.to.SearchProductTo;
import lombok.Data;

import java.util.List;

/**
 * es返回的vo
 */
@Data
public class SearchResult {
    //es保存的所有sku信息
    private List<SearchProductTo> products;
    //分页信息
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPage;//总页码
    //查询到的结果所涉及的所有品牌
    private List<BrandVo> brands;
    //查询到的结果所涉及的所有分类
    private List<CatalogVo> catalogs;
    //查询到的结果所涉及的所有属性
    private List<AttrVo> attrs;
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }
    @Data
    public static class CatalogVo{
        private Long catelogId;
        private String catelogName;
    }
    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
