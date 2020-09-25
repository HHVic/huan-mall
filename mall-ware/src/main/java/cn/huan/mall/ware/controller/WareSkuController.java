package cn.huan.mall.ware.controller;

import cn.huan.common.to.SkuStockTo;
import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.R;
import cn.huan.mall.ware.entity.WareSkuEntity;
import cn.huan.mall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品库存
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 02:02:08
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 通过skuid上查询是否有库存
     *
     * @param skuIds
     * @return List<HasStockTo></></>
     */
    @PostMapping("/hasStock")
    public R hasStock(@RequestBody List<Long> skuIds) {
        List<SkuStockTo> stockTo = wareSkuService.hasStock(skuIds);
        Map<Long,Integer> map = null;
        if (!CollectionUtils.isEmpty(stockTo)) {
            map = stockTo.stream().collect(Collectors.toMap(SkuStockTo::getSkuId,SkuStockTo::getStock));
        }
        return R.ok().addData(map);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
