package cn.huan.mall.product.service;

import cn.huan.mall.product.vo.Catelog2List;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithChildren();

    void removeByCids(List<Long> asList);

    Long[] getCategoryPath(Long catelogId);

    void updateDetail(CategoryEntity category);

    List<CategoryEntity> getChildren(Long parentId);

    Map<String, List<Catelog2List>> catalog2List();
}

