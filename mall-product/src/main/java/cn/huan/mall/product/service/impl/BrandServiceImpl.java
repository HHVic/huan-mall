package cn.huan.mall.product.service.impl;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.product.dao.BrandDao;
import cn.huan.mall.product.entity.BrandEntity;
import cn.huan.mall.product.entity.CategoryBrandRelationEntity;
import cn.huan.mall.product.service.BrandService;
import cn.huan.mall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isBlank(key)) {
            wrapper.eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
        //更新自己
        baseMapper.updateById(brand);
        if(!StringUtils.isEmpty(brand.getName())){
            //如果更新了品牌名，需要更新相关联的表
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
            //TODO 需要更新其他
        }
    }

    @Override
    public List<BrandEntity> listBrandCategoryRelaiton(Long catId) {
        List<CategoryBrandRelationEntity> relationEntityList = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId)
        );
        List<BrandEntity> brandEntities = relationEntityList.stream().map(entity -> {
            Long brandId = entity.getBrandId();
            BrandEntity brandEntity = baseMapper.selectById(brandId);
            return brandEntity;
        }).collect(Collectors.toList());
        return brandEntities;
    }

}