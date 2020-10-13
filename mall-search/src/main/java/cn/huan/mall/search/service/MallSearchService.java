package cn.huan.mall.search.service;

import cn.huan.mall.search.vo.SearchParam;
import cn.huan.mall.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam searchParam);
}
