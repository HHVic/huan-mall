package cn.huan.mall.coupon.service.impl;

import cn.huan.common.to.MemberPrice;
import cn.huan.common.to.SkuReductionTo;
import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.coupon.dao.SkuFullReductionDao;
import cn.huan.mall.coupon.entity.MemberPriceEntity;
import cn.huan.mall.coupon.entity.SkuFullReductionEntity;
import cn.huan.mall.coupon.entity.SkuLadderEntity;
import cn.huan.mall.coupon.service.MemberPriceService;
import cn.huan.mall.coupon.service.SkuFullReductionService;
import cn.huan.mall.coupon.service.SkuLadderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 保存 sms_sku_ladder 信息
        Long skuId = skuReductionTo.getSkuId();
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
        skuLadderEntity.setSkuId(skuId);
        skuLadderService.save(skuLadderEntity);
        //保存 sms_sku_full_reduction 信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setSkuId(skuId);
        BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
        skuFullReductionService.save(skuFullReductionEntity);
        //保存会员价格信息 sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        if(!CollectionUtils.isEmpty(memberPrice)){
            List<MemberPriceEntity> memberPriceEntities = memberPrice.stream().map(member -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setMemberLevelId(member.getId());
                memberPriceEntity.setMemberLevelName(member.getName());
                memberPriceEntity.setMemberPrice(member.getPrice());
                memberPriceEntity.setSkuId(skuId);
                return memberPriceEntity;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(memberPriceEntities);
        }
    }

}