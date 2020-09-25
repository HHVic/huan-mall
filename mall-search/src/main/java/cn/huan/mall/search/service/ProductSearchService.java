package cn.huan.mall.search.service;

import cn.huan.common.to.SearchProductTo;

import java.util.List;

public interface ProductSearchService {

    void saveSpuInfos(List<SearchProductTo> searchProductTos);
}
