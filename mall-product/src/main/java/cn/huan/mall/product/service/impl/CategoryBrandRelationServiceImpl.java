package cn.huan.mall.product.service.impl;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.product.dao.CategoryBrandRelationDao;
import cn.huan.mall.product.entity.CategoryBrandRelationEntity;
import cn.huan.mall.product.service.BrandService;
import cn.huan.mall.product.service.CategoryBrandRelationService;
import cn.huan.mall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> listCategory(Long brandId) {
        List<CategoryBrandRelationEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return entities;
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandName(name);
        UpdateWrapper<CategoryBrandRelationEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("brand_id",brandId);
        baseMapper.update(categoryBrandRelationEntity, wrapper);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogName(name);
        UpdateWrapper<CategoryBrandRelationEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("catelog_id",catId);
        baseMapper.update(categoryBrandRelationEntity, wrapper);
    }

}