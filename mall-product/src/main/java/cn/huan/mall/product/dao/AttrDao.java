package cn.huan.mall.product.dao;

import cn.huan.mall.product.entity.AttrEntity;
import cn.huan.mall.product.vo.ItemDescVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> getSearchableId();

    List<ItemDescVo.BaseAttrWithGroup> getListWithGroupBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
