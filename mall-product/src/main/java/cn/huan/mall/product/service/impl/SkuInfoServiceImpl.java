package cn.huan.mall.product.service.impl;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.product.dao.SkuInfoDao;
import cn.huan.mall.product.entity.SkuImagesEntity;
import cn.huan.mall.product.entity.SkuInfoEntity;
import cn.huan.mall.product.entity.SpuInfoDescEntity;
import cn.huan.mall.product.service.*;
import cn.huan.mall.product.vo.ItemDescVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String catalogId = (String) params.get("catelogId");
        if (catalogId != null && !"0".equals(catalogId)) {
            wrapper.eq("catalog_id", catalogId);
        }

        String brandId = (String) params.get("brandId");
        if (brandId != null && !"0".equals(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.eq("sku_id", key)
                        .or().like("sku_name", key)
                        .or().like("sku_desc", key)
                        .or().like("sku_title",key)
                        .or().like("sku_subtitle",key);
            });
        }

        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }

        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max) && !"0".equals(max)){
            wrapper.le("price",max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public ItemDescVo itemDesc(Long skuId) throws ExecutionException, InterruptedException {
        ItemDescVo itemDesc = new ItemDescVo();
        //使用异步编排  skuInfo skuImages 单独 spu相关信息需要skuInfo查询完毕后执行
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //1.查询skuInfo信息
            SkuInfoEntity skuInfo = baseMapper.selectById(skuId);
            itemDesc.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            //2.查询sku对应的所有图片信息
            List<SkuImagesEntity> skuImages = skuImagesService.getListBySkuId(skuId);
            itemDesc.setSkuImages(skuImages);
        }, executor);

        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //3.查询sku对应的spu的所有销售属性信息
            List<ItemDescVo.SaleAttr> saleAttrs = skuSaleAttrValueService.getListWithReferredSkusBySpuId(skuInfo.getSpuId());
            itemDesc.setSaleAttrs(saleAttrs);
        }, executor);

        CompletableFuture<Void> spuDescFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //5.查询spu详情信息
            List<SpuInfoDescEntity> spuInfoDesc = spuInfoDescService.getListBySpuId(skuInfo.getSpuId());
            itemDesc.setSpuInfoDesc(spuInfoDesc);
        }, executor);

        CompletableFuture<Void> baseAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //4.查询sku对应的spu的所有属性分组以及基本属性信息
            List<ItemDescVo.BaseAttrWithGroup> baseAttrWithGroups = attrService.getListWithGroupBySpuId(skuInfo.getSpuId(), skuInfo.getCatalogId());
            itemDesc.setBaseAttrs(baseAttrWithGroups);
        }, executor);

        //所有任务执行完才可返回
        CompletableFuture.allOf(skuImagesFuture,saleAttrFuture,spuDescFuture,baseAttrFuture).get();

        return itemDesc;
    }

}