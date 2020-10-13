package cn.huan.mall.search.api;

import cn.huan.common.to.SearchProductTo;
import cn.huan.common.utils.R;
import cn.huan.mall.search.service.ProductSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("search/product")
public class ProductSearchController {
    @Autowired
    private ProductSearchService productSearchService;

    @PostMapping("/saveSpuInfos")
    public R saveSpuInfos(@RequestBody List<SearchProductTo> searchProductTos){
        productSearchService.saveSpuInfos(searchProductTos);
        return R.ok();
    }
}
