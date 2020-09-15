package cn.huan.mall.product.dao;

import cn.huan.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
