package cn.huan.mall.search;

import cn.huan.common.to.SkuStockTo;
import cn.huan.mall.search.config.ElasticSearchConfig;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class MallSearchApplicationTests {

	@Autowired
	private RestHighLevelClient client;

	@Test
	void contextLoads() {
		List<SkuStockTo> skuStockTos = new ArrayList<>();
		SkuStockTo stockTo1 = new SkuStockTo();
		stockTo1.setSkuId(10l);
		stockTo1.setStock(10);
		SkuStockTo stockTo2 = new SkuStockTo();
		stockTo2.setSkuId(12l);
		stockTo2.setStock(12);
		SkuStockTo stockTo3 = new SkuStockTo();
		stockTo3.setSkuId(13l);
		stockTo3.setStock(13);
		skuStockTos.add(stockTo1);
		skuStockTos.add(stockTo2);
		skuStockTos.add(stockTo3);
		Map<Long, Integer> collect = skuStockTos.stream().collect(Collectors.toMap(SkuStockTo::getSkuId, SkuStockTo::getStock));
		System.out.println(collect);
	}

	@Test
	public void indexData() throws IOException {
		IndexRequest indexRequest = new IndexRequest ("users");
		User user = new User();
		user.setUserName("张三");
		user.setAge(20);
		user.setGender("男");
		String jsonString = JSON.toJSONString(user);
		//设置要保存的内容
		indexRequest.source(jsonString, XContentType.JSON);
		//执行创建索引和保存数据
		IndexResponse index = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);

		System.out.println(index);

	}
	@Data
	class User {
		private String userName;
		private int age;
		private String gender;
	}

}
