package cn.huan.mall.ware.controller;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.R;
import cn.huan.mall.ware.entity.PurchaseEntity;
import cn.huan.mall.ware.service.PurchaseService;
import cn.huan.mall.ware.vo.PurchaseDoneVo;
import cn.huan.mall.ware.vo.PurchaseItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 采购信息
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 02:02:08
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 合并采购需求 /ware/purchase/merge
     */
    @PostMapping("/merge")
    public R merge(@RequestBody PurchaseItemVo vo){
        purchaseService.merge(vo);
        return R.ok();
    }

    /**
     * 查询未领取的订单 /ware/purchase/unreceive/list
     */
    @GetMapping("/unreceive/list")
    public R listUnReceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnReceive(params);

        return R.ok().put("page", page);
    }

    /**
     * 领取采购单 /ware/purchase/received
     * 采购人员点击领取
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> purchaseIds){
        purchaseService.purchaseReceive(purchaseIds);
        return R.ok();
    }

    /**
     * 完成采购 /ware/purchase/done
     * 采购人员点击领取
     */
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVo doneVo){
        purchaseService.purchaseDone(doneVo);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
