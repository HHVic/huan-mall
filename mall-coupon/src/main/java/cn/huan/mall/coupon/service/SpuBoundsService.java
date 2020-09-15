package cn.huan.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:55:13
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

