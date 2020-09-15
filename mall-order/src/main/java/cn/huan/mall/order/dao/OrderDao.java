package cn.huan.mall.order.dao;

import cn.huan.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 02:00:47
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
