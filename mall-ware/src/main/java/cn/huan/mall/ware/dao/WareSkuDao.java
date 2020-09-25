package cn.huan.mall.ware.dao;

import cn.huan.common.to.SkuStockTo;
import cn.huan.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 02:02:08
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void updateStock(@Param("skuId") Long skuId,
                     @Param("wareId") Long wareId,
                     @Param("skuNum") Integer skuNum);

    List<SkuStockTo> hasStock(@Param("skuIds") List<Long> skuIds);
}
