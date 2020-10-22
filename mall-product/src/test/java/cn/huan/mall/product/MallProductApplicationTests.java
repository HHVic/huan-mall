package cn.huan.mall.product;

import cn.huan.mall.product.entity.BrandEntity;
import cn.huan.mall.product.service.AttrService;
import cn.huan.mall.product.service.BrandService;
import cn.huan.mall.product.service.CategoryService;
import cn.huan.mall.product.service.SkuSaleAttrValueService;
import cn.huan.mall.product.vo.ItemDescVo;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private AttrService attrService;


    @Test
    public void testSaleAttr(){
        List<ItemDescVo.SaleAttr> saleAttrList = skuSaleAttrValueService.getListWithReferredSkusBySpuId(16L);
        System.out.println(saleAttrList);
    }

    @Test
    public void testBaseAttr(){
        System.out.println(attrService.getListWithGroupBySpuId(16L, 225L));
    }

    @Test
    public void testRedis(){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("hello","word");
        System.out.println(ops.get("hello"));

    }

    @Test
    public void testRedisson(){
        RLock lock = redissonClient.getLock("lock");
        lock.lock();
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("xiaomi");
        brandEntity.setName("xiaomi");
        brandService.save(brandEntity);
    }

    @Test
    public void categoryPath(){
        Long[] categoryPath = categoryService.getCategoryPath(165l);
        System.out.println(Arrays.toString(categoryPath));
    }

}
