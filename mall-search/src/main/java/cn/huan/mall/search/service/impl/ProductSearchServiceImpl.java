package cn.huan.mall.search.service.impl;

import cn.huan.common.to.SearchProductTo;
import cn.huan.mall.search.config.ElasticSearchConfig;
import cn.huan.mall.search.service.ProductSearchService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ProductSearchServiceImpl implements ProductSearchService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public void saveSpuInfos(List<SearchProductTo> searchProductTos) {

        BulkRequest bulkRequest = new BulkRequest();
        for (SearchProductTo searchProductTo : searchProductTos){
            IndexRequest indexRequest = new IndexRequest("product");
            indexRequest.id(searchProductTo.getSkuId().toString());
            String jsonString = JSON.toJSONString(searchProductTo);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        try {
            client.bulk(bulkRequest,ElasticSearchConfig.COMMON_OPTIONS);
            log.info("商品保存成功");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("商品保存失败");
            e.printStackTrace();
        }
    }
}
