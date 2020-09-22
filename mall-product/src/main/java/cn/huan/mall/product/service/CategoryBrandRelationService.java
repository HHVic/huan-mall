package cn.huan.mall.product.service;

import cn.huan.common.utils.PageUtils;
import cn.huan.mall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> listCategory(Long brandId);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);
}

