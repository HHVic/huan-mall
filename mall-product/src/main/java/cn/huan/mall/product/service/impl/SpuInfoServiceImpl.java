package cn.huan.mall.product.service.impl;

import cn.huan.common.constant.ProductConstant;
import cn.huan.common.to.SkuReductionTo;
import cn.huan.common.to.SpuBoundsTo;
import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.common.utils.R;
import cn.huan.mall.product.dao.SpuInfoDao;
import cn.huan.mall.product.entity.*;
import cn.huan.mall.product.feign.CouponFeignService;
import cn.huan.mall.product.service.*;
import cn.huan.mall.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //保存spu的基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        Long catalogId = spuInfoEntity.getCatalogId();
        Long brandId = spuInfoEntity.getBrandId();

        //保存spu详细信息 pms_spu_info_desc
        SpuInfoDescEntity infoDescEntity = new SpuInfoDescEntity();
        infoDescEntity.setSpuId(spuId);
        List<String> decript = vo.getDecript();
        if (!CollectionUtils.isEmpty(decript)) {
            String string = StringUtils.join(decript, ',');
            infoDescEntity.setDecript(string);
        }
        spuInfoDescService.save(infoDescEntity);

        //保存spu图片信息 pms_spu_images
        List<String> images = vo.getImages();
        if (!CollectionUtils.isEmpty(images)) {
            List<SpuImagesEntity> spuImagesEntities = images.stream().map(img -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                spuImagesEntity.setSpuId(spuId);
                spuImagesEntity.setImgUrl(img);
                return spuImagesEntity;
            }).collect(Collectors.toList());
            spuImagesService.saveBatch(spuImagesEntities);
        }

        //保存spu基本属性 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setAttrId(baseAttr.getAttrId());
                AttrEntity attrEntity = attrService.getById(baseAttr.getAttrId());
                if (attrEntity != null) {
                    productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                }
                productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
                productAttrValueEntity.setSpuId(spuId);
                productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(productAttrValueEntities);
        }
        //保存spu 优惠信息 mall_sms -> sms_spu_bounds
        Bounds bounds = vo.getBounds();
        if (bounds != null) {
            SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
            spuBoundsTo.setSpuId(spuId);
            BeanUtils.copyProperties(bounds,spuBoundsTo);
            //feign调用远程服务
            R res = couponFeignService.saveSpuCoupon(spuBoundsTo);
            if(!res.isOk()){
                log.error("调用远程服务保存优惠券信息失败");
            }
        }

        //保存sku的基本信息 pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setCatalogId(catalogId);
                skuInfoEntity.setBrandId(brandId);
                //默认图片
                List<Images> skuImgs = sku.getImages();
                String defaultImg = "";
                if (!CollectionUtils.isEmpty(skuImgs)) {
                    List<Images> defImg = skuImgs.stream()
                            .filter(skuImg -> skuImg.getDefaultImg() == ProductConstant.Image.DEFAULT_IMG.getCode())
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(defImg)) defaultImg = defImg.get(0).getImgUrl();
                }
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //设置详细信息
                List<String> descar = sku.getDescar();
                String desc = "";
                if (!CollectionUtils.isEmpty(descar)) {
                    desc = StringUtils.join(descar,',');
                }
                skuInfoEntity.setSkuDesc(desc);
                skuInfoService.save(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                //保存sku图片信息 pms_sku_images
                if (!CollectionUtils.isEmpty(skuImgs)) {
                    List<SkuImagesEntity> skuImagesEntities = skuImgs.stream().filter(skuImg -> !StringUtils.isEmpty(skuImg.getImgUrl())).map(skuImg -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(skuImg, skuImagesEntity);
                        skuImagesEntity.setSkuId(skuId);
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
                    skuImagesService.saveBatch(skuImagesEntities);
                }
                //保存sku销售属性 pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                if (!CollectionUtils.isEmpty(attrs)) {
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuId);
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                }
                //保存折扣信息 mall_sms -> sms_sku_ladder sms_sku_full_reduction sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                R res = couponFeignService.saveSkuReduction(skuReductionTo);
                if(!res.isOk()){
                    log.error("调用远程服务保存满减信息失败");
                }
            });
        }


    }

}