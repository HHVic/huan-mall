package cn.huan.mall.product.web;

import cn.huan.mall.product.service.SkuInfoService;
import cn.huan.mall.product.vo.ItemDescVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String itemDesc(@PathVariable("skuId")Long skuId, Model model) throws ExecutionException, InterruptedException {
        System.out.println("查询商品" + skuId + "详情");
        ItemDescVo item = skuInfoService.itemDesc(skuId);
        model.addAttribute("item",item);
        return "item";
    }
}
