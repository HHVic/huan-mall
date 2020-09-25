package cn.huan.mall.product.feign;

import cn.huan.common.to.SearchProductTo;
import cn.huan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-search")
public interface SearchFeignService {

    @PostMapping("search/product/saveSpuInfos")
    R saveSpuInfos(@RequestBody List<SearchProductTo> searchProductTos);
}
