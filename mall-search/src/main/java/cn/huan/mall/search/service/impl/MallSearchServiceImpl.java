package cn.huan.mall.search.service.impl;

import cn.huan.common.to.SearchProductTo;
import cn.huan.mall.search.config.ElasticSearchConfig;
import cn.huan.mall.search.constant.EsConstant;
import cn.huan.mall.search.service.MallSearchService;
import cn.huan.mall.search.vo.SearchParam;
import cn.huan.mall.search.vo.SearchResult;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam searchParam) {

        //封装查询dql
        SearchRequest searchRequest = searchRequest(searchParam);
        try {
            SearchResponse search = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            return searchResult(search, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SearchResult searchResult(SearchResponse search, SearchParam searchParam) {
        SearchResult result = new SearchResult();
        SearchHits hits = search.getHits();
        List<SearchProductTo> products = new ArrayList<>();
        //包装products
        if (hits != null) {
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                String source = hit.getSourceAsString();
                SearchProductTo product = JSON.parseObject(source, SearchProductTo.class);
                products.add(product);
                //如果使用关键字检索，高亮
                String keyword = searchParam.getKeyword();
                if(!StringUtils.isEmpty(keyword)){
                    String skuTitle = hit.getHighlightFields().get("skuTitle").fragments()[0].string();
                    product.setSkuTitle(skuTitle);
                }
            }
        }
        result.setProducts(products);
        //获取所有的聚合
        Aggregations aggregations = search.getAggregations();
        //包装catalogs
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        List<? extends Terms.Bucket> catalogAggBuckets = catalog_agg.getBuckets();
        if(!CollectionUtils.isEmpty(catalogAggBuckets)){
            catalogAggBuckets.forEach(bucket -> {
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
                catalogVo.setCatelogId(bucket.getKeyAsNumber().longValue());
                ParsedStringTerms catalogNameAggregations = bucket.getAggregations().get("catalog_name_agg");
                catalogVo.setCatelogName(catalogNameAggregations.getBuckets().get(0).getKeyAsString());
                catalogVos.add(catalogVo);
            });
        }
        result.setCatalogs(catalogVos);

        //包装brands
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brandAggBuckets = brand_agg.getBuckets();
        if(!CollectionUtils.isEmpty(brandAggBuckets)){
            brandAggBuckets.forEach(bucket -> {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
                ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
                ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
                brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());
                brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
                brandVos.add(brandVo);
            });
        }
        result.setBrands(brandVos);

        //包装attrs
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = aggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attr_id_agg.getBuckets();
        if(!CollectionUtils.isEmpty(attrIdAggBuckets)){
            attrIdAggBuckets.forEach(bucket -> {
                SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
                ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
                attrVo.setAttrName(attr_name_agg.getBuckets().get(0).getKeyAsString());
                ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
                List<String> attrValues = attr_value_agg.getBuckets().stream().map(bucket1 -> bucket1.getKeyAsString()).collect(Collectors.toList());
                attrVo.setAttrValue(attrValues);
                attrVos.add(attrVo);
            });
        }
        result.setAttrs(attrVos);

        //分页信息
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        result.setPageNum(searchParam.getPageNum());
        result.setTotalPage((int) (total + EsConstant.PRODUCT_PAGE_SIZE - 1) / EsConstant.PRODUCT_PAGE_SIZE);
        return result;
    }

    private SearchRequest searchRequest(SearchParam searchParam) {
        SearchSourceBuilder source = new SearchSourceBuilder();
        //query - bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //must
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", keyword));
        }
        //filter - catalogId
        Long catalog3Id = searchParam.getCatalog3Id();
        if (catalog3Id != null) {
            boolQuery.filter(QueryBuilders.termQuery("catelogId", catalog3Id));
        }
        //filter - hasStock
        Integer hasStock = searchParam.getHasStock();
        if(hasStock != null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock", hasStock == 1));
        }
        //filter - brand
        List<Long> brands = searchParam.getBrandId();
        if (!CollectionUtils.isEmpty(brands)) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", brands));
        }
        //filter - skuPrice
        String skuPrice = searchParam.getSkuPrice();
        if (!StringUtils.isEmpty(skuPrice)) {
            String[] s = skuPrice.split("_");
            System.out.println("拆分长度:" + s.length);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");

            if (skuPrice.startsWith("_") && s.length >= 2) {
                rangeQuery.lte(s[1]);
            } else if (skuPrice.endsWith("_")) {
                rangeQuery.gte(s[0]);
            } else {
                if (s.length >= 2) {
                    rangeQuery.gte(s[0]);
                    rangeQuery.lte(s[1]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        //filter - attrs
        List<String> attrs = searchParam.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            for (String attr : attrs) {
                //4_TAS-AN00:TES-AN00
                String[] s = attr.split("_");
                //s[0]=attrId,s[1]=TAS-AN00:TES-AN00
                String[] values = s[1].split(":");
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                query.must(QueryBuilders.termQuery("attrs.attrId", s[0]));
                query.must(QueryBuilders.termsQuery("attrs.attrValue", values));
                boolQuery.filter(QueryBuilders.nestedQuery("attrs", query, ScoreMode.None));
            }
        }
        SearchSourceBuilder query = source.query(boolQuery);
        //sort  skuPrice_asc/desc  saleCount_asc/desc  hotScore_asc/desc
        String sort = searchParam.getSort();
        if (!StringUtils.isEmpty(sort)) {
            String[] s = sort.split("_");
            query.sort(s[0], "asc".equalsIgnoreCase(s[1]) ? SortOrder.ASC : SortOrder.DESC);
        }
        //高亮
        if (!StringUtils.isEmpty(keyword)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            query.highlighter(highlightBuilder);
        }
        //分页
        query.from((searchParam.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        query.size(EsConstant.PRODUCT_PAGE_SIZE);
        //聚合 - catalog
        TermsAggregationBuilder catalogAggregation = AggregationBuilders.terms("catalog_agg").field("catelogId").size(50);
        catalogAggregation.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catelogName").size(1));
        query.aggregation(catalogAggregation);
        //聚合 - brand
        TermsAggregationBuilder brandAggregation = AggregationBuilders.terms("brand_agg");
        brandAggregation.field("brandId").size(50);
        //brand 子聚合 brandName
        brandAggregation.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        //brand 子聚合 brandImg
        brandAggregation.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        query.aggregation(brandAggregation);
        //聚合 - attrs
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        //聚合属性id
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50);
        //聚合属性名
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //聚合属性值
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nested.subAggregation(attr_id_agg);
        query.aggregation(nested);
        //System.out.println("查询DQL:" + query.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, query);
    }
}
