package cn.huan.mall.product.web;

import cn.huan.mall.product.entity.CategoryEntity;
import cn.huan.mall.product.service.CategoryService;
import cn.huan.mall.product.vo.Catelog2List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(value = {"/","/index"})
    public String index(Model model){
        //查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getChildren(0L);
        model.addAttribute("categories",categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String,List<Catelog2List>> catalog2List(){
        Map<String,List<Catelog2List>> map = categoryService.catalog2List();
        return map;
    }
}
