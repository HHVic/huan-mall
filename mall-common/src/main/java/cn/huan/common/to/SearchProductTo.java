package cn.huan.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchProductTo {

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catelogId;

    private String brandName;

    private String brandImg;

    private String catelogName;

    private List<Attr> attrs;


    @Data
    public static class Attr{
        private Long attrId;

        private String attrName;

        private String attrValue;
    }

}
