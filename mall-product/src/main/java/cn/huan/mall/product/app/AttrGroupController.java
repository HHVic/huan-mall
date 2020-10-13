package cn.huan.mall.product.app;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.R;
import cn.huan.mall.product.entity.AttrAttrgroupRelationEntity;
import cn.huan.mall.product.entity.AttrEntity;
import cn.huan.mall.product.entity.AttrGroupEntity;
import cn.huan.mall.product.service.AttrAttrgroupRelationService;
import cn.huan.mall.product.service.AttrGroupService;
import cn.huan.mall.product.service.AttrService;
import cn.huan.mall.product.service.CategoryService;
import cn.huan.mall.product.vo.AttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @GetMapping("/{attrGroupId}/attr/relation")
    public R listAttrGroupRelation(@PathVariable Long attrGroupId){
        List<AttrEntity> attrEntities = attrService.listByGroupId(attrGroupId);
        return R.ok().put("data",attrEntities);
    }

//    /product/attrgroup/{attrgroupId}/noattr/relation
//    获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R listNoAttrRelation(@PathVariable Long attrGroupId,
                                @RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPageNoAttrRelation(params,attrGroupId);
        return R.ok().put("page", page);
    }

//    /product/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R saveRelation(@RequestBody List<AttrAttrgroupRelationEntity> relationEntities){
        relationService.saveBatch(relationEntities);
        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody List<AttrAttrgroupRelationEntity> relationEntities){
        relationService.removeBatch(relationEntities);
        return R.ok();
    }

//    /product/attrgroup/{catelogId}/withattr
    @GetMapping("/{catelogId}/withattr")
    public R listWithAttr(@PathVariable Long catelogId){
        List<AttrGroupVo> attrGroupVos = attrGroupService.listWithAttrByCatelogId(catelogId);
        return R.ok().put("data",attrGroupVos);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Long categoryId) {
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,categoryId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        //查询三级分类信息
        Long[] categoryPath = categoryService.getCategoryPath(attrGroup.getCatelogId());
        attrGroup.setCategoryPath(categoryPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
