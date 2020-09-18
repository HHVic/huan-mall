package cn.huan.mall.product.service.impl;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.product.dao.CategoryDao;
import cn.huan.mall.product.entity.CategoryEntity;
import cn.huan.mall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取所有分类信息以及子分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithChildren() {
        //获取所有分类信息
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<>());
        //获取所有一级分类
        List<CategoryEntity> root = categoryEntities.stream()
                .filter(category -> category.getParentCid() == 0)
                .map(categoryEntity -> getWithChildren(categoryEntity,categoryEntities))
                .sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort())))
                .collect(Collectors.toList());
        return root;
    }

    @Override
    public void removeByCids(List<Long> cIds) {
        //TODO 删除功能实现
        baseMapper.deleteBatchIds(cIds);
    }

    /**
     * 递归获取所有的分类及其子分类
     * @param current 当前分类
     * @param all 所有类类
     * @return 包装当前分类
     */
    private CategoryEntity getWithChildren(CategoryEntity current, List<CategoryEntity> all) {
        List<CategoryEntity> categoryEntities = all.stream()
                .filter(category -> Objects.equals(category.getParentCid(), current.getCatId()))
                .map(categoryEntity -> getWithChildren(categoryEntity,all))
                .sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort())))
                .collect(Collectors.toList());
        current.setChildren(categoryEntities);
        return current;
    }

}