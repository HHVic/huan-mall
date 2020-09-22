package cn.huan.mall.product.feign;

import cn.huan.common.to.SkuReductionTo;
import cn.huan.common.to.SpuBoundsTo;
import cn.huan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-coupon")
public interface CouponFeignService {
    /**
     * 使用feign调用coupon服务保存spu优惠券信息
     * @param spuBoundsTo
     * @return R
     */
    @RequestMapping("coupon/spubounds/save")
    R saveSpuCoupon(@RequestBody SpuBoundsTo spuBoundsTo);

    /**
     * 使用feign调用coupon服务保存sku满减信息
     * @param skuReductionTo
     * @return
     */
    @RequestMapping("coupon/skufullreduction/save")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
