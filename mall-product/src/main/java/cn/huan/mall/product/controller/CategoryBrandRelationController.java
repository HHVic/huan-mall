package cn.huan.mall.product.controller;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.R;
import cn.huan.mall.product.entity.BrandEntity;
import cn.huan.mall.product.entity.CategoryBrandRelationEntity;
import cn.huan.mall.product.service.BrandService;
import cn.huan.mall.product.service.CategoryBrandRelationService;
import cn.huan.mall.product.service.CategoryService;
import cn.huan.mall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/catelog/list")
    public R listCategory(@RequestParam Long brandId){
        List<CategoryBrandRelationEntity> categoryBrandRelationEntity =
                categoryBrandRelationService.listCategory(brandId);
        return R.ok().put("data",categoryBrandRelationEntity);
    }

//    /product/categorybrandrelation/brands/list
//    获取指定分类下所有品牌列表
    @GetMapping("/brands/list")
    public R listBrandCategoryRelaiton(@RequestParam Long catId){
        List<BrandEntity> brandEntities = brandService.listBrandCategoryRelaiton(catId);
        List<BrandVo> brandVos = brandEntities.stream().map(entity -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(entity.getBrandId());
            brandVo.setBrandName(entity.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",brandVos);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        //设置数据库品牌名和分类名
        categoryBrandRelation.setCatelogName(
                categoryService.getById(categoryBrandRelation.getCatelogId()).getName()
        );
        categoryBrandRelation.setBrandName(
                brandService.getById(categoryBrandRelation.getBrandId()).getName()
        );
		categoryBrandRelationService.save(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
