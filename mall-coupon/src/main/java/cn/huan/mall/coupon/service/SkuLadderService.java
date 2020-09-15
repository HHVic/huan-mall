package cn.huan.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:55:13
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

