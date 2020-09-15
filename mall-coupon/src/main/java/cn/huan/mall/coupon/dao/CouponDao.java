package cn.huan.mall.coupon.dao;

import cn.huan.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:55:13
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
