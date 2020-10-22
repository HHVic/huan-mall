package cn.huan.mall.product.dao;

import cn.huan.mall.product.entity.SkuSaleAttrValueEntity;
import cn.huan.mall.product.vo.ItemDescVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<ItemDescVo.SaleAttr> getListWithReferredSkusBySpuId(@Param("spuId") Long spuId);
}
