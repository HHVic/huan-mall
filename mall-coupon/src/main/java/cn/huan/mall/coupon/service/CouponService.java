package cn.huan.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:55:13
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

