package cn.huan.mall.product.vo;

import cn.huan.mall.product.entity.SkuImagesEntity;
import cn.huan.mall.product.entity.SkuInfoEntity;
import cn.huan.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

//用于展示商品详情信息
@Data
public class ItemDescVo {
    //1.skuInfo
    private SkuInfoEntity skuInfo;
    //2.skuImages
    private List<SkuImagesEntity> skuImages;
    //3.spu对应的所有销售属性
    private List<SaleAttr> saleAttrs;
    //4.spu详情
    private List<SpuInfoDescEntity> spuInfoDesc;
    //5.spu 属性分组以及基本属性
    private List<BaseAttrWithGroup> baseAttrs;
    @Data
    public static class SaleAttr{
        private Long attrId;
        private String attrName;
        private List<attrValueWithSku> attrValue;

        @Data
        public static class attrValueWithSku{
            private String attrValue;
            private String referredSku;
        }
    }
    @Data
    public static class BaseAttrWithGroup{
        private String groupName;
        private List<BaseAttrInfo> attrInfos;

        @Data
        public static class BaseAttrInfo{
            private String attrName;
            private String attrValue;
        }

    }
}
